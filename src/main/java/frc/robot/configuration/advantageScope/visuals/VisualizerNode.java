package frc.robot.configuration.advantageScope.visuals;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import frc.robot.configuration.constants.ModuleConstants.TurretConstants;

public class VisualizerNode {

    private final Supplier<Pose2d> robotPoseSupplier;
    private final DoubleSupplier turretAngleSupplier; 
    private final DoubleSupplier hoodAngleSupplier;   

    private final StructPublisher<Pose3d> turretPublisher;
    private final StructPublisher<Pose3d> hoodPublisher;

    // ---------------------------------------------------------
    // 📏 AJUSTE DE COORDENADAS
    // ---------------------------------------------------------
    
    // 1. ROBOT A TORRETA
    // X = 0.65 (Me dijiste que estaba adelante)
    // Y = 0.00 (Si está centrada a lo ancho del robot)
    // Z = 0.35 (La altura desde el piso hasta la base naranja)
    private final Translation3d ROBOT_TO_TURRET = new Translation3d(0.15, 0, 0); 

    // 2. TORRETA A HOOD
    // X = 0.0 (Si el pivote del hood está alineado verticalmente con el centro de la torreta)
    //     *Si el hood está un poco más adelante del centro de rotación, ponle valor aquí.
    // Y = 0.0
    // Z = 0.20 (Qué tan arriba de la base naranja está el eje del hood)
    private final Translation3d TURRET_TO_HOOD = new Translation3d(0.0, 0.0, 0); 

    public VisualizerNode(Supplier<Pose2d> robotPose, DoubleSupplier turretAngle, DoubleSupplier hoodAngle) {
        this.robotPoseSupplier = robotPose;
        this.turretAngleSupplier = turretAngle;
        this.hoodAngleSupplier = hoodAngle;

        var table = NetworkTableInstance.getDefault().getTable("Visualizer");
        this.turretPublisher = table.getStructTopic("TurretPose", Pose3d.struct).publish();
        this.hoodPublisher = table.getStructTopic("HoodPose", Pose3d.struct).publish();
    }

    public void periodic() {
        Pose2d robotPose2d = Pose2d.kZero;
        double turretDegrees = turretAngleSupplier.getAsDouble();
        double hoodDegrees = hoodAngleSupplier.getAsDouble();

        // 1. Base del Robot (Ojo: AdvantageScope usa Pose3d para todo en 3D)
        Pose3d robotPose3d = new Pose3d(robotPose2d);

        // 2. Torreta
        Transform3d turretTransform = new Transform3d(
            ROBOT_TO_TURRET, 
            new Rotation3d(0, 0, Math.toRadians(turretDegrees)) // Gira en Z (Yaw)
        );

         

        // 3. Hood
        Transform3d hoodTransform = new Transform3d(
            TURRET_TO_HOOD, 
            new Rotation3d(0, Math.toRadians(hoodDegrees), 0) // Gira en Y (Pitch)
        );
        //Pose3d hoodPose = turretPose.transformBy(hoodTransform);
        
       // Pose3d turretPose = new Pose3d(new Pose2d(robotPose2d.getTranslation(), new Rotation2d(Math.toRadians(turretDegrees))));
        Pose3d turretPose = new Pose3d().transformBy(turretTransform);

        // Publicar
        turretPublisher.set(turretPose);
        //hoodPublisher.set(hoodPose);
    }
}