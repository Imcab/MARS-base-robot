// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.advantageScope.visuals.nodes.trajectory;

import com.stzteam.mars.services.Query;
import edu.wpi.first.math.geometry.Pose2d;

public class TrajectoryQuery implements Query {
  public final Pose2d robotPose;
  public final double turretAngle;
  public final double velocityMPS;

  public TrajectoryQuery(Pose2d robotPose, double turretAngle, double velocityMPS) {
    this.robotPose = robotPose;
    this.turretAngle = turretAngle;
    this.velocityMPS = velocityMPS;
  }
}
