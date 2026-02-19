package frc.robot.configuration.advantageScope.visualsNode.trajectory;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import mars.source.models.nodes.Node;
import mars.source.models.nodes.NodeMessage;

public class TrajectoryNode extends Node<TrajectoryNode.TrajectoryMsg> {

    public static class TrajectoryMsg extends NodeMessage<TrajectoryMsg> {
        
        // ¡OJO! Usamos un Arreglo ([]) para dibujar la línea completa
        public Pose3d[] trajectory = new Pose3d[0];

        private final StructArrayPublisher<Pose3d> publisher;

        public TrajectoryMsg() {
            var table = NetworkTableInstance.getDefault().getTable("Visualizer");
            // Publicamos un ARRAY de Structs
            this.publisher = table.getStructArrayTopic("ShotTrajectory", Pose3d.struct).publish();
        }

        @Override
        public void telemeterize(String tableName) {
            publisher.set(trajectory);
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
            DoubleSupplier velocityMPS, // Quitamos HoodAngle
            Consumer<TrajectoryMsg> topicPublisher) {
        
        super(name, new TrajectoryMsg(), topicPublisher);
        
        // Constructor actualizado del driver
        this.driver = new TrajectoryDriver(robotPose, turretAngle, velocityMPS);
    }

    @Override
    protected void updateHardware() {
        driver.updateData(messagePayload);
    }
}