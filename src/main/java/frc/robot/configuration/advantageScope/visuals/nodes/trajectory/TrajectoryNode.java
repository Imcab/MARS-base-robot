package frc.robot.configuration.advantageScope.visuals.nodes.trajectory;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.stzteam.forgemini.io.NetworkIO;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import mars.source.services.Service;
import mars.source.services.nodes.Node;
import mars.source.services.nodes.NodeMessage;

public class TrajectoryNode extends Node<TrajectoryNode.TrajectoryMsg> implements Service<TrajectoryQuery, TrajectoryReply> {

    public static class TrajectoryMsg extends NodeMessage<TrajectoryMsg> {
        public Pose3d[] trajectory = new Pose3d[0];

        @Override
        public void telemeterize(String tableName) {

            NetworkIO.set(tableName, "ShotTrajectory", trajectory);
        }
    }

    private final TrajectoryPhysics physics = new TrajectoryPhysics();
    
    private final Supplier<Pose2d> robotPoseSupplier;
    private final DoubleSupplier turretAngleSupplier;
    private final DoubleSupplier velocitySupplier;

    public TrajectoryNode(
            String name, 
            Supplier<Pose2d> robotPose,
            DoubleSupplier turretAngle, 
            DoubleSupplier velocity,
            Consumer<TrajectoryMsg> topicPublisher) {
        super(name, new TrajectoryMsg(), topicPublisher);
        this.robotPoseSupplier = robotPose;
        this.turretAngleSupplier = turretAngle;
        this.velocitySupplier = velocity;
    }

    @Override
    public TrajectoryReply execute(TrajectoryQuery query) {
        if (isFallback()) return new TrajectoryReply(new Pose3d[0]);

        physics.calculate(messagePayload, query.robotPose, query.turretAngle, query.velocityMPS);
        
        return new TrajectoryReply(messagePayload.trajectory);
    }

    @Override
    protected void processInformation() {

        physics.calculate(
            messagePayload, 
            robotPoseSupplier.get(), 
            turretAngleSupplier.getAsDouble(), 
            velocitySupplier.getAsDouble()
        );
    }

    public Pose3d[] getLatestPath() {
        return messagePayload.trajectory;
    }
}