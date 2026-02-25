package frc.robot.configuration.advantageScope.visuals.nodes.visualizer;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.geometry.Pose3d;
import mars.source.services.nodes.Node;
import mars.source.services.nodes.NodeMessage;


public class VisualizerNode extends Node<VisualizerNode.VisualizerMsg> {

    public static class VisualizerMsg extends NodeMessage<VisualizerMsg> {
        public Pose3d turretPose = new Pose3d();
        public Pose3d hoodPose = new Pose3d();
        public Pose3d intakePose = new Pose3d();

        @Override
        public void telemeterize(String tableName) {

            com.stzteam.forgemini.io.NetworkIO.set(tableName, "TurretComponent", turretPose);
            com.stzteam.forgemini.io.NetworkIO.set(tableName, "HoodComponent", hoodPose);
            com.stzteam.forgemini.io.NetworkIO.set(tableName, "IntakeComponent", intakePose);
        }
    }

    private final VisualizerLogic logic = new VisualizerLogic();
    
    private final DoubleSupplier turretAngle;
    private final DoubleSupplier hoodAngle;
    private final DoubleSupplier intakeAngle;

    public VisualizerNode(
            String name, 
            DoubleSupplier turretAngle, 
            DoubleSupplier hoodAngle, 
            DoubleSupplier intakeAngle,
            Consumer<VisualizerMsg> topicPublisher) {
        
        super(name, new VisualizerMsg(), topicPublisher);
        this.turretAngle = turretAngle;
        this.hoodAngle = hoodAngle;
        this.intakeAngle = intakeAngle;
    }

  
    @Override
    protected void processInformation() {

        logic.update(
            messagePayload, 
            turretAngle.getAsDouble(), 
            hoodAngle.getAsDouble(), 
            intakeAngle.getAsDouble()
        );
    }
}
