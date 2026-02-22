package frc.robot.configuration.advantageScope.visuals.nodes;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import com.stzteam.forgemini.io.NetworkIO;

import edu.wpi.first.math.geometry.Pose3d;
import frc.robot.configuration.advantageScope.visuals.drivers.VisualizerDriver;
import mars.source.models.nodes.Node;
import mars.source.models.nodes.NodeMessage;

public class VisualizerNode extends Node<VisualizerNode.VisualizerMsg> {

    public static class VisualizerMsg extends NodeMessage<VisualizerMsg> {
        
        public Pose3d turretPose = new Pose3d();
        public Pose3d hoodPose = new Pose3d();
        public Pose3d intakePose = new Pose3d();

        @Override
        public void telemeterize(String tableName) {
  
            NetworkIO.set(tableName, "TurretComponent", turretPose);
            NetworkIO.set(tableName, "HoodComponent", hoodPose);
            NetworkIO.set(tableName, "IntakeComponent", intakePose);

        }
    }

    public interface VisualizerIO {
        void updateData(VisualizerMsg data);
    }

    protected final VisualizerIO hardwareDriver;

    public VisualizerNode(String name, 
                          DoubleSupplier turretAngle, 
                          DoubleSupplier hoodAngle, 
                          DoubleSupplier intakeAngle,
                          Consumer<VisualizerMsg> topicPublisher) {
        
        super(name, new VisualizerMsg(), topicPublisher);
        
        this.hardwareDriver = new VisualizerDriver(turretAngle, hoodAngle, intakeAngle);
    }

    @Override
    protected void updateHardware() {
        hardwareDriver.updateData(messagePayload);
    }
}