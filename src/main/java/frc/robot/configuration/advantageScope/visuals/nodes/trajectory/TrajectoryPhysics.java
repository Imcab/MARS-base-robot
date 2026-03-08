// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.advantageScope.visuals.nodes.trajectory;

import edu.wpi.first.math.geometry.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode.TrajectoryMsg;
import frc.robot.helpers.AllianceUtil;
import java.util.ArrayList;
import java.util.List;

public class TrajectoryPhysics {
  public Translation3d HUB_LOCATION = new Translation3d(4.63, 4.04, 1.90);
  private final double GRAVITY = 9.81;
  private final double TIME_STEP = 0.05;
  private final Translation3d SHOOTER_OFFSET = new Translation3d(0.15, 0.0, 0.44);
  private Translation3d hub = Translation3d.kZero;

  public void calculate(TrajectoryMsg data, Pose2d robotPose, double turretDeg, double shotSpeed) {
    if (shotSpeed < 1.0) shotSpeed = 10.0;

    DriverStation.getAlliance()
        .ifPresent(
            allianceColor -> {
              if (allianceColor == Alliance.Red) {

                this.hub = AllianceUtil.flip(HUB_LOCATION);
              } else {
                this.hub = HUB_LOCATION;
              }
            });

    Rotation2d combinedYaw = robotPose.getRotation().plus(Rotation2d.fromDegrees(turretDeg));
    Translation3d startPoint =
        new Translation3d(robotPose.getX(), robotPose.getY(), 0.0)
            .plus(SHOOTER_OFFSET.rotateBy(new Rotation3d(0, 0, combinedYaw.getRadians())));

    double dx = hub.getX() - startPoint.getX();
    double dy = hub.getY() - startPoint.getY();
    double dz = hub.getZ() - startPoint.getZ();
    double horizontalDist = Math.sqrt(dx * dx + dy * dy);

    double timeOfFlight = horizontalDist / (shotSpeed * 0.9);
    double vx = dx / timeOfFlight;
    double vy = dy / timeOfFlight;
    double vz = (dz + 0.5 * GRAVITY * Math.pow(timeOfFlight, 2)) / timeOfFlight;

    List<Pose3d> path = new ArrayList<>();
    for (double t = 0; t <= timeOfFlight; t += TIME_STEP) {
      double currX = startPoint.getX() + vx * t;
      double currY = startPoint.getY() + vy * t;
      double currZ = startPoint.getZ() + vz * t - 0.5 * GRAVITY * Math.pow(t, 2);
      path.add(new Pose3d(currX, currY, currZ, new Rotation3d()));
    }
    path.add(new Pose3d(hub, new Rotation3d()));
    data.trajectory = path.toArray(new Pose3d[0]);
  }
}
