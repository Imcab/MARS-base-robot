package frc.robot.core.modules.swerve.visionNode.limelight;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.configuration.constants.ModuleConstants.VisionConstants;
import frc.robot.core.modules.swerve.visionNode.VisionNode;
import frc.robot.core.modules.swerve.visionNode.VisionNode.VisionIO;
import frc.robot.core.modules.swerve.visionNode.VisionNode.VisionMsg;
import frc.robot.helpers.LimelightHelpers;

public class LimelightDriver implements VisionIO {

    private final String cameraName;
    private final Supplier<Rotation2d> yawSupplier;
    private final DoubleSupplier yawRateSupplier;

    public LimelightDriver(String cameraName, Supplier<Rotation2d> yawSupplier, DoubleSupplier yawRateSupplier) {
        this.cameraName = cameraName;
        this.yawSupplier = yawSupplier;
        this.yawRateSupplier = yawRateSupplier;
    }

    @Override
    public void updateData(VisionMsg data) {
        data.hasTarget = LimelightHelpers.getTV(cameraName);
        
        double currentYaw = yawSupplier.get().getDegrees();
        double currentYawRate = yawRateSupplier.getAsDouble();

        LimelightHelpers.SetRobotOrientation(cameraName, currentYaw, 0, 0, 0, 0, 0);
        LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(cameraName);
        
        if (mt2 == null || mt2.tagCount == 0 || Math.abs(currentYawRate) > VisionConstants.MAX_ANGULAR_VELOCITY_DEG_PER_SEC) {
            data.validPose = false;
            return;
        }

        double xyStdDev = (mt2.tagCount >= 2) ? VisionConstants.MULTI_TAG_STD_DEV : 
            calculateSingleTagStdDev(mt2.avgTagDist);

        if (mt2.avgTagDist > VisionConstants.MAX_VALID_DISTANCE_METERS && mt2.tagCount < 2) {
            data.validPose = false;
            return;
        }

        data.botPose = mt2.pose;
        data.timestamp = mt2.timestampSeconds;
        data.validPose = true;
        data.stdDevs = VecBuilder.fill(xyStdDev, xyStdDev, VisionConstants.ROTATION_STD_DEV);
    }

    private double calculateSingleTagStdDev(double distance) {
        return VisionConstants.SINGLE_TAG_BASE_STD_DEV + 
               (Math.pow(distance, 2) * VisionConstants.SINGLE_TAG_DISTANCE_MULTIPLIER);
    }
}