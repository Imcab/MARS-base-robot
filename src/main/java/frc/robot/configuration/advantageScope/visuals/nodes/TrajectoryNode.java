package frc.robot.configuration.advantageScope.visuals.nodes;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.stzteam.forgemini.io.NetworkIO;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import frc.robot.configuration.advantageScope.visuals.drivers.TrajectoryDriver;
import mars.source.models.nodes.Node;
import mars.source.models.nodes.NodeMessage;

public class TrajectoryNode extends Node<TrajectoryNode.TrajectoryMsg> {

    public static class TrajectoryMsg extends NodeMessage<TrajectoryMsg> {
        
        public Pose3d[] trajectory = new Pose3d[0];

        @Override
        public void telemeterize(String tableName) {
            NetworkIO.set(tableName, "ShotTrajectory", trajectory);
        }
    }

    public interface TrajectoryIO {
        void updateData(TrajectoryMsg data);
    }

    protected final TrajectoryIO driver;

    public TrajectoryNode(
            String name, 
            Supplier<Pose2d> robotPose,
            DoubleSupplier turretAngle, 
            DoubleSupplier velocityMPS,
            Consumer<TrajectoryMsg> topicPublisher) {
        
        super(name, new TrajectoryMsg(), topicPublisher);
        
        this.driver = new TrajectoryDriver(robotPose, turretAngle, velocityMPS);

    }

    public Pose3d[] getTrajectory() {
        return messagePayload.trajectory;
    }

    @Override
    protected void updateHardware() {
        driver.updateData(messagePayload);
    }
}