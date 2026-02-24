package frc.robot.core.modules.swerve.visionNode;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.configuration.constants.ModuleConstants.VisionConstants;
import frc.robot.core.modules.swerve.visionNode.VisionNode.VisionIO;
import frc.robot.core.modules.swerve.visionNode.VisionNode.VisionMsg;

public class VisionSimDriver implements VisionIO {

    @Override
    public void updateData(VisionMsg data) {

        data.hasTarget = false;
        data.validPose = false;
        
        data.botPose = Pose2d.kZero;
        data.timestamp = Timer.getFPGATimestamp();
        
        data.stdDevs = VisionConstants.DEFAULT_STD_DEVS;
    }
}
