// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.advantageScope.visuals.nodes.gamepiece;

import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.mars.services.Service;
import com.stzteam.mars.services.nodes.Node;
import com.stzteam.mars.services.nodes.NodeMessage;
import edu.wpi.first.math.geometry.Pose3d;
import java.util.function.Consumer;

public class GamePieceNode extends Node<GamePieceNode.GamePieceMsg>
    implements Service<GamePieceQuery, GamePieceReply> {

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
