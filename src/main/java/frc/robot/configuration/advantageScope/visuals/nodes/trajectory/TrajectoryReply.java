package frc.robot.configuration.advantageScope.visuals.nodes.trajectory;

import edu.wpi.first.math.geometry.Pose3d;
import mars.source.services.Reply;

public class TrajectoryReply implements Reply {
    public final Pose3d[] path;

    public TrajectoryReply(Pose3d[] path) {
        this.path = path;
    }
}
