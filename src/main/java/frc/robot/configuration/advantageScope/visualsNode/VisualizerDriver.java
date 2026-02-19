package frc.robot.configuration.advantageScope.visualsNode;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;

import frc.robot.configuration.advantageScope.visualsNode.VisualizerNode.VisualizerIO;
import frc.robot.configuration.advantageScope.visualsNode.VisualizerNode.VisualizerMsg;

public class VisualizerDriver implements VisualizerIO {

    private final DoubleSupplier turretAngleSupplier;
    private final DoubleSupplier hoodAngleSupplier;

    // --- CONSTANTES FÍSICAS (Copiadas de tu código funcionando) ---
    private final Translation3d TURRET_OFFSET = new Translation3d(0.15, 0, 0);
    private final Translation3d PIVOT_LOCATION = new Translation3d(0.09, 0.0, 0.40);

    public VisualizerDriver(DoubleSupplier turretAngleSupplier, DoubleSupplier hoodAngleSupplier) {
        this.turretAngleSupplier = turretAngleSupplier;
        this.hoodAngleSupplier = hoodAngleSupplier;
    }

    @Override
    public void updateData(VisualizerMsg data) {
        // 1. Obtener valores actuales
        double turretDeg = turretAngleSupplier.getAsDouble();
        double hoodDeg = hoodAngleSupplier.getAsDouble();

        // 2. Calcular Torreta
        Rotation3d turretRot = new Rotation3d(0, 0, Math.toRadians(turretDeg));
        Pose3d turretPose = new Pose3d(TURRET_OFFSET, turretRot);

        // 3. Calcular Hood (Cadena Cinemática)
        Pose3d hoodPose = turretPose
            .transformBy(new Transform3d(PIVOT_LOCATION, new Rotation3d()))
            .transformBy(new Transform3d(Translation3d.kZero, new Rotation3d(0, Math.toRadians(hoodDeg), 0)));

        // 4. Llenar el Mensaje (Msg)
        data.turretPose = turretPose;
        data.hoodPose = hoodPose;
    }
}