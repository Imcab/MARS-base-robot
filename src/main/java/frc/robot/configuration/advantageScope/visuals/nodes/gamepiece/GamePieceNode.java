package frc.robot.configuration.advantageScope.visuals.nodes.gamepiece;

import java.util.function.Consumer;

import com.stzteam.forgemini.io.NetworkIO;

import edu.wpi.first.math.geometry.Pose3d;
import mars.source.services.Service;
import mars.source.services.nodes.Node;
import mars.source.services.nodes.NodeMessage;

public class GamePieceNode extends Node<GamePieceNode.GamePieceMsg> implements Service<GamePieceQuery, GamePieceReply> {

    public static class GamePieceMsg extends NodeMessage<GamePieceMsg> {
        public Pose3d[] visualizerPose = new Pose3d[0];

        @Override
        public void telemeterize(String tableName) {
            NetworkIO.set(tableName, "FlyingFuel", visualizerPose);
        }
    }

    private final GamePieceAnimator animator = new GamePieceAnimator();
    private Pose3d[] latestTrajectory = new Pose3d[0];
    private boolean triggerRequested = false;

    public GamePieceNode(String name, Consumer<GamePieceMsg> topicPublisher) {
        super(name, new GamePieceMsg(), topicPublisher);
    }

    @Override
    public GamePieceReply execute(GamePieceQuery query) {
        if (isFallback()) return new GamePieceReply(false);
        
        this.triggerRequested = query.fireTrigger;
        this.latestTrajectory = query.trajectory;
        return new GamePieceReply(true);
    }

    @Override
    protected void processInformation() {
        animator.updateAnimation(messagePayload, triggerRequested, latestTrajectory);
        triggerRequested = false;
    }
}