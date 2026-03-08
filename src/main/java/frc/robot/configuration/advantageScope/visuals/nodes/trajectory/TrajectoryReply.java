// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.advantageScope.visuals.nodes.trajectory;

import com.stzteam.mars.services.Reply;
import edu.wpi.first.math.geometry.Pose3d;

public class TrajectoryReply implements Reply {
  public final Pose3d[] path;

  public TrajectoryReply(Pose3d[] path) {
    this.path = path;
  }
}
