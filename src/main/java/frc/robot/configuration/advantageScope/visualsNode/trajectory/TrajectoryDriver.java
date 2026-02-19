package frc.robot.configuration.advantageScope.visualsNode.trajectory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import frc.robot.configuration.advantageScope.visualsNode.trajectory.TrajectoryNode.TrajectoryIO;
import frc.robot.configuration.advantageScope.visualsNode.trajectory.TrajectoryNode.TrajectoryMsg;

public class TrajectoryDriver implements TrajectoryIO {

    private final Supplier<Pose2d> robotPoseSupplier;
    private final DoubleSupplier turretAngleSupplier;
    private final DoubleSupplier velocitySupplier;

    // ---------------------------------------------------------
    // 🎯 TU TARGET FIJO (Copiado de tu solicitud)
    // ---------------------------------------------------------
    public static final Translation3d HUB_LOCATION = new Translation3d(4.63, 4.04, 1.90);

    // Configuración Física
    private final double GRAVITY = 9.81;
    private final double TIME_STEP = 0.05; 
    
    // Offset de la punta del cañón (Debe coincidir con tu Visualizer)
    private final Translation3d SHOOTER_ORIGIN_OFFSET = new Translation3d(0.15, 0.0, 0.40); 

    public TrajectoryDriver(
        Supplier<Pose2d> robotPose, 
        DoubleSupplier turretAngle, 
        DoubleSupplier velocitySupplier
    ) {
        this.robotPoseSupplier = robotPose;
        this.turretAngleSupplier = turretAngle;
        this.velocitySupplier = velocitySupplier;
    }

    @Override
    public void updateData(TrajectoryMsg data) {
        // 1. Dónde está el robot
        Pose2d robotPose = robotPoseSupplier.get();
        double turretDeg = turretAngleSupplier.getAsDouble();
        double shotSpeed = velocitySupplier.getAsDouble(); // m/s (Ej: 20)

        // Evitamos dividir por cero si la velocidad es muy baja
        if (shotSpeed < 1.0) shotSpeed = 10.0;

        // 2. Calcular el PUNTO DE SALIDA (Start Point)
        //    Rotamos el offset por la suma de (Robot Yaw + Torreta Yaw)
        Rotation2d combinedYaw = robotPose.getRotation().plus(Rotation2d.fromDegrees(turretDeg));
        
        Translation3d startPoint = new Translation3d(robotPose.getX(), robotPose.getY(), 0.0)
            .plus(SHOOTER_ORIGIN_OFFSET.rotateBy(new Rotation3d(0, 0, combinedYaw.getRadians())));

        // 3. MATEMÁTICA INVERSA: "Solving for Vz" 🧠
        //    Queremos ir de Start -> Hub.
        
        // A. Distancias
        double dx = HUB_LOCATION.getX() - startPoint.getX();
        double dy = HUB_LOCATION.getY() - startPoint.getY();
        double dz = HUB_LOCATION.getZ() - startPoint.getZ();
        
        double horizontalDist = Math.sqrt(dx*dx + dy*dy);

        // B. Tiempo de Vuelo Estimado (Time of Flight)
        //    Asumimos que la velocidad horizontal es dominante.
        //    Esto nos da un tiempo "T" realista para llegar al target a esa velocidad.
        double timeOfFlight = horizontalDist / (shotSpeed * 0.9); // El 0.9 compensa un poco la resistencia/ángulo

        // C. Velocidades Necesarias para llegar en tiempo T
        //    Vx y Vy son constantes (movimiento uniforme)
        double vx = dx / timeOfFlight;
        double vy = dy / timeOfFlight;
        
        //    Vz Inicial: Usamos la fórmula de cinemática: z_f = z_i + vi*t - 0.5*g*t^2
        //    Despejamos vi: vi = (z_f - z_i + 0.5*g*t^2) / t
        double vz = (dz + 0.5 * GRAVITY * timeOfFlight * timeOfFlight) / timeOfFlight;

        // 4. GENERAR LA CURVA (Simulación)
        List<Pose3d> path = new ArrayList<>();
        double currentX = startPoint.getX();
        double currentY = startPoint.getY();
        double currentZ = startPoint.getZ();
        
        // Simular el vuelo
        for (double t = 0; t <= timeOfFlight; t += TIME_STEP) {
            // Guardamos el punto
            path.add(new Pose3d(currentX, currentY, currentZ, new Rotation3d()));

            // Actualizamos posición (Euler Integration)
            // Nota: Para visualización suave, mejor usamos la fórmula directa
            currentX = startPoint.getX() + vx * t;
            currentY = startPoint.getY() + vy * t;
            currentZ = startPoint.getZ() + vz * t - 0.5 * GRAVITY * t * t;
        }

        // Aseguramos que el último punto sea EXACTAMENTE el Hub (para que se vea conectado)
        path.add(new Pose3d(HUB_LOCATION, new Rotation3d()));

        data.trajectory = path.toArray(new Pose3d[0]);
    }

}