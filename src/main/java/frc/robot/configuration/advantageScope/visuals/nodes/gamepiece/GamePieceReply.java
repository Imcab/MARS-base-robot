package frc.robot.configuration.advantageScope.visuals.nodes.gamepiece;

import com.stzteam.mars.services.Reply;

public class GamePieceReply implements Reply {
    public final boolean isProcessing;

    public GamePieceReply(boolean isProcessing) {
        this.isProcessing = isProcessing;
    }
}