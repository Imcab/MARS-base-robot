// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.advantageScope.visuals.nodes.gamepiece;

import edu.wpi.first.math.geometry.Pose3d;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode.GamePieceMsg;

public class GamePieceAnimator {
  private boolean isFlying = false;
  private Pose3d[] activeTrajectory;
  private int animationIndex = 0;

  public void updateAnimation(GamePieceMsg data, boolean trigger, Pose3d[] trajectory) {
    if (trigger && !isFlying && trajectory != null && trajectory.length > 0) {
      this.activeTrajectory = trajectory;
      this.isFlying = true;
      this.animationIndex = 0;
    }

    if (isFlying) {
      if (animationIndex < activeTrajectory.length) {
        data.visualizerPose = new Pose3d[] {activeTrajectory[animationIndex]};
        animationIndex++;
      } else {
        isFlying = false;
        data.visualizerPose = new Pose3d[0];
      }
    } else {
      data.visualizerPose = new Pose3d[0];
    }
  }
}
