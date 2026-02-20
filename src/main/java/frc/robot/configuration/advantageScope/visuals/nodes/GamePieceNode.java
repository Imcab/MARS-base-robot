package frc.robot.configuration.advantageScope.visuals.nodes;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.stzteam.forgemini.io.NetworkIO;

import edu.wpi.first.math.geometry.Pose3d;
import frc.robot.configuration.advantageScope.visuals.drivers.GamePieceDriver;
import mars.source.models.nodes.Node;
import mars.source.models.nodes.NodeMessage;

public class GamePieceNode extends Node<GamePieceNode.GamePieceMsg> {

    public static class GamePieceMsg extends NodeMessage<GamePieceMsg> {
        
        public Pose3d[] visualizerPose = new Pose3d[0];

        @Override
        public void telemeterize(String tableName) {
            NetworkIO.set(tableName, "FlyingFuel", visualizerPose);
        }
    }

    public interface GamePieceIO {
        void updateData(GamePieceMsg data);
    }

    protected final GamePieceIO driver;

    public GamePieceNode(
            String name, 
            Supplier<Pose3d[]> trajectorySource, 
            BooleanSupplier shootTrigger, 
            Consumer<GamePieceMsg> topicPublisher) {
        
        super(name, new GamePieceMsg(), topicPublisher);
        this.driver = new GamePieceDriver(trajectorySource, shootTrigger);
    }

    @Override
    protected void updateHardware() {
        driver.updateData(messagePayload);
    }
}