package frc.robot.core.modules.swerve.visionNode;

import java.util.function.Consumer;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import frc.robot.configuration.KeyManager.CommonTables;
import frc.robot.configuration.KeyManager.CommonTables.Terminology;
import frc.robot.configuration.constants.ModuleConstants.VisionConstants;
import mars.source.services.Service;
import mars.source.services.nodes.Node;
import mars.source.services.nodes.NodeMessage;

public abstract class VisionNode extends Node<VisionNode.VisionMsg> implements Service<VisionQuery, VisionReply> {

    public static class VisionMsg extends NodeMessage<VisionMsg> {
        public boolean hasTarget = false;
        public Pose2d botPose = new Pose2d();
        public double timestamp = 0.0;
        public boolean validPose = false;
        public Matrix<N3, N1> stdDevs = VisionConstants.DEFAULT_STD_DEVS;

        @Override
        public void telemeterize(String tableName) {
            com.stzteam.forgemini.io.NetworkIO.set(tableName, Terminology.HAS + CommonTables.TARGET_KEY, hasTarget);
            com.stzteam.forgemini.io.NetworkIO.set(tableName, CommonTables.ROBOT_KEY + CommonTables.POSE_KEY, botPose);
            com.stzteam.forgemini.io.NetworkIO.set(tableName, CommonTables.TIMESTAMP_KEY, timestamp);
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
    public VisionReply execute(VisionQuery query) {
        if (isFallback() || !messagePayload.validPose) {
            return new VisionReply(false, new Pose2d(), 0, VisionConstants.DEFAULT_STD_DEVS);
        }
        return new VisionReply(
            messagePayload.hasTarget, 
            messagePayload.botPose, 
            messagePayload.timestamp, 
            messagePayload.stdDevs
        );
    }

    @Override
    protected void processInformation() {
        hardwareDriver.updateData(messagePayload);
    }
}