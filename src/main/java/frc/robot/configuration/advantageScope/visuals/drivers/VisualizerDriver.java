package frc.robot.configuration.advantageScope.visuals.drivers;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import frc.robot.configuration.advantageScope.visuals.nodes.VisualizerNode.VisualizerIO;
import frc.robot.configuration.advantageScope.visuals.nodes.VisualizerNode.VisualizerMsg;

public class VisualizerDriver implements VisualizerIO {

    private final DoubleSupplier turretAngleSupplier;
    private final DoubleSupplier hoodAngleSupplier;

    private final Translation3d TURRET_OFFSET = new Translation3d(0.15, 0, 0);
    private final Translation3d PIVOT_LOCATION = new Translation3d(0.09, 0.0, 0.40);

    public VisualizerDriver(DoubleSupplier turretAngleSupplier, DoubleSupplier hoodAngleSupplier) {
        this.turretAngleSupplier = turretAngleSupplier;
        this.hoodAngleSupplier = hoodAngleSupplier;
    }

    @Override
    public void updateData(VisualizerMsg data) {

        double turretDeg = turretAngleSupplier.getAsDouble();
        double hoodDeg = hoodAngleSupplier.getAsDouble();

        Rotation3d turretRot = new Rotation3d(0, 0, Math.toRadians(turretDeg));
        Pose3d turretPose = new Pose3d(TURRET_OFFSET, turretRot);

        Pose3d hoodPose = turretPose
            .transformBy(new Transform3d(PIVOT_LOCATION, new Rotation3d()))
            .transformBy(new Transform3d(Translation3d.kZero, new Rotation3d(0, Math.toRadians(hoodDeg), 0)));

        data.turretPose = turretPose;
        data.hoodPose = hoodPose;
    }
}