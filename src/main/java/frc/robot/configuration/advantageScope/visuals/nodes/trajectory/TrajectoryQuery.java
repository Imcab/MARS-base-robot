package frc.robot.configuration.advantageScope.visuals.nodes.trajectory;

import edu.wpi.first.math.geometry.Pose2d;
import mars.source.services.Query;

public class TrajectoryQuery implements Query {
    public final Pose2d robotPose;
    public final double turretAngle;
    public final double velocityMPS;

    public TrajectoryQuery(Pose2d robotPose, double turretAngle, double velocityMPS) {
        this.robotPose = robotPose;
        this.turretAngle = turretAngle;
        this.velocityMPS = velocityMPS;
    }
}
