package frc.robot.core.modules.swerve.nodes.drivers;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.core.modules.swerve.nodes.VisionNode.VisionIO;
import frc.robot.core.modules.swerve.nodes.VisionNode.VisionMsg;

public class SimDriver implements VisionIO {
        @Override
        public void updateData(VisionMsg data) {
            data.hasTarget = false;
            data.validPose = false;
            data.botPose = Pose2d.kZero;
            data.timestamp = Timer.getFPGATimestamp();
        }
    }
