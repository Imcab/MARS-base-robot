package frc.robot.core.modules.swerve.nodes.drivers;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.configuration.constants.VisionConstants;
import frc.robot.core.modules.swerve.nodes.VisionNode.VisionIO;
import frc.robot.core.modules.swerve.nodes.VisionNode.VisionMsg;
import frc.robot.helpers.LimelightHelpers;

public class LimelightDriver implements VisionIO{

    private final String cameraName;
    private final Supplier<Rotation2d> yawSupplier;
    private final DoubleSupplier yawRateSupplier; 
    protected boolean validPose = false;

    public LimelightDriver(String cameraName, Supplier<Rotation2d> yawSupplier, DoubleSupplier yawRateSupplier) {
        this.cameraName = cameraName;
        this.yawSupplier = yawSupplier;
        this.yawRateSupplier = yawRateSupplier;
    }

    @Override
    public void updateData(VisionMsg data) {

        data.hasTarget = LimelightHelpers.getTV(cameraName);
        data.validPose = false;

        double currentYaw = yawSupplier.get().getDegrees();
        double currentYawRate = yawRateSupplier.getAsDouble();

        LimelightHelpers.SetRobotOrientation(cameraName, currentYaw, 0, 0, 0, 0, 0);
        LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(cameraName);
        
        
        if (mt2 == null || mt2.tagCount == 0) return;

        if (Math.abs(currentYawRate) > VisionConstants.MAX_ANGULAR_VELOCITY_DEG_PER_SEC) return;

        double xyStdDev;

        if (mt2.tagCount >= 2) {
            //Usamos la constante de multi-tag
            xyStdDev = VisionConstants.MULTI_TAG_STD_DEV; 
        } else {
            double distance = mt2.avgTagDist;
            
            //Usamos el límite de distancia de las constantes
            if (distance > VisionConstants.MAX_VALID_DISTANCE_METERS) {
                return; 
            }

            //Aplicamos la fórmula heurística con las constantes
            xyStdDev = VisionConstants.SINGLE_TAG_BASE_STD_DEV + 
                       (Math.pow(distance, 2) * VisionConstants.SINGLE_TAG_DISTANCE_MULTIPLIER); 
        }

        data.botPose = mt2.pose;
        data.timestamp = mt2.timestampSeconds;
        data.validPose = true;

        data.stdDevs = VecBuilder.fill(xyStdDev, xyStdDev, VisionConstants.ROTATION_STD_DEV);

    }

    public double getDistanceToAprilTag() {

        double[] targetPose = LimelightHelpers.getTargetPose_CameraSpace(cameraName);


        if (LimelightHelpers.getTV(cameraName)) {
        
            double distanceZ = targetPose[2]; 

            return distanceZ; 
        }

        return 0.0;
    }

}
