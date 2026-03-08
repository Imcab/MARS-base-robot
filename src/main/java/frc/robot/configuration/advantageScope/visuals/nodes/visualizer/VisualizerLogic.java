// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.advantageScope.visuals.nodes.visualizer;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import frc.robot.configuration.advantageScope.visuals.nodes.visualizer.VisualizerNode.VisualizerMsg;

public class VisualizerLogic {

  private final Translation3d TURRET_OFFSET = new Translation3d(0.15, 0, 0);
  private final Translation3d PIVOT_LOCATION = new Translation3d(0.09, 0.0, 0.40);
  private final Translation3d INTAKE_LOCATION = new Translation3d(-0.30, 0, 0.285);

  public void update(VisualizerMsg data, double turretDeg, double hoodDeg, double intakeDeg) {

    Rotation3d turretRot = new Rotation3d(0, 0, Math.toRadians(turretDeg));
    Pose3d turretPose = new Pose3d(TURRET_OFFSET, turretRot);

    Pose3d intakePose =
        new Pose3d(INTAKE_LOCATION, new Rotation3d(0, Math.toRadians(-intakeDeg), 0));

    Pose3d hoodPose =
        turretPose
            .transformBy(new Transform3d(PIVOT_LOCATION, new Rotation3d()))
            .transformBy(
                new Transform3d(
                    Translation3d.kZero, new Rotation3d(0, Math.toRadians(hoodDeg), 0)));

    data.turretPose = turretPose;
    data.hoodPose = hoodPose;
    data.intakePose = intakePose;
  }
}
