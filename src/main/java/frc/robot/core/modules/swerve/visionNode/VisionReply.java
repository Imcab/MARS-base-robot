package frc.robot.core.modules.swerve.visionNode;

import com.stzteam.mars.services.Reply;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

public class VisionReply implements Reply {
    public final boolean hasTarget;
    public final Pose2d botPose;
    public final double timestamp;
    public final Matrix<N3, N1> stdDevs;

    public VisionReply(boolean hasTarget, Pose2d botPose, double timestamp, Matrix<N3, N1> stdDevs) {
        this.hasTarget = hasTarget;
        this.botPose = botPose;
        this.timestamp = timestamp;
        this.stdDevs = stdDevs;
    }
}
