// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.constants.ModuleConstants;

import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathConstraints;
import edu.wpi.first.math.util.Units;
import frc.robot.core.modules.swerve.SwerveRequestFactory;

public class SwerveConstants {

  public static final PathConstraints pathConstraints =
      new PathConstraints(4.5, 4.0, Units.degreesToRadians(540), Units.degreesToRadians(720));

  public static final PPHolonomicDriveController pathplannerPID =
      new PPHolonomicDriveController(
          new PIDConstants(5.0, 0.0, 0.0), new PIDConstants(5.0, 0.0, 0.0));

  public static final double MaxSpeed = SwerveRequestFactory.MaxSpeed;
  public static final double MaxAngularRate = SwerveRequestFactory.MaxAngularRate;

  public static final double crossMovementSpeed = 0.5;
}
