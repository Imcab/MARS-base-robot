// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.advantageScope.visuals.nodes.gamepiece;

import com.stzteam.mars.services.Reply;

public class GamePieceReply implements Reply {
  public final boolean isProcessing;

  public GamePieceReply(boolean isProcessing) {
    this.isProcessing = isProcessing;
  }
}
