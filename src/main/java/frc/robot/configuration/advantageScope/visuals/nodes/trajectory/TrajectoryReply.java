package frc.robot.configuration.advantageScope.visuals.nodes.trajectory;

import com.stzteam.mars.services.Reply;

import edu.wpi.first.math.geometry.Pose3d;

public class TrajectoryReply implements Reply {
    public final Pose3d[] path;

    public TrajectoryReply(Pose3d[] path) {
        this.path = path;
    }
}
