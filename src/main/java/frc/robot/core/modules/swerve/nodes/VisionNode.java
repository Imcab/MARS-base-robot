package frc.robot.core.modules.swerve.nodes;

import java.util.function.Consumer;

import com.stzteam.forgemini.io.NetworkIO;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import frc.robot.configuration.constants.VisionConstants;
import mars.source.models.nodes.Node;
import mars.source.models.nodes.NodeMessage;

public class VisionNode extends Node<VisionNode.VisionMsg>{

    public static class VisionMsg extends NodeMessage<VisionMsg> {

        public boolean hasTarget = false;
        public Pose2d botPose = new Pose2d();
        public double timestamp = 0.0;
        public boolean validPose = false;
        public Matrix<N3,N1> stdDevs = VisionConstants.DEFAULT_STD_DEVS;

        @Override
        public void telemeterize(String tableName) {

            NetworkIO.set(tableName, "HasTarget", hasTarget);
            
            NetworkIO.set(tableName, "BotPose", botPose);
   
            NetworkIO.set(tableName, "Timestamp", timestamp);
        }
    }

    public interface VisionIO {
        void updateData(VisionMsg data);
    }

    protected final VisionIO hardwareDriver;

    public VisionNode(String name, VisionIO hardwareDriver, Consumer<VisionMsg> topicPublisher) {
        super(name, new VisionMsg(), topicPublisher);
        this.hardwareDriver = hardwareDriver;
    }

    @Override
    protected void updateHardware() {
        hardwareDriver.updateData(messagePayload);
    }
    
}
