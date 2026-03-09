// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.constants;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;

public class Constants {

  public static final double INTAKE_TOLERANCE = 2;
  public static final double TURRET_TOLERANCE = 4;
  public static final double FLYWHEEL_TOLERANCE = 250;
  public static final double ARM_TOLERANCE = 2;

  public static final double kSimLoopPeriod = 0.004;

  public static final Rotation2d kBlueAlliancePerspectiveRotation = Rotation2d.kZero;
  public static final Rotation2d kRedAlliancePerspectiveRotation = Rotation2d.k180deg;

  public static final Translation3d HUB_LOCATION = new Translation3d(4.63, 4.10, 1.9);

  public static final InterpolatingDoubleTreeMap INTERPOLATION_MAP =
      new InterpolatingDoubleTreeMap();

  public static final InterpolatingDoubleTreeMap RPM_MAP = new InterpolatingDoubleTreeMap();

  static {
    RPM_MAP.put(2.03329389, -2350.0);
    RPM_MAP.put(2.59498157, -2488.0);
    RPM_MAP.put(2.88109361, -2695.0);
    RPM_MAP.put(3.79530137, -2800.0);
    RPM_MAP.put(4.39056989, -2980.0);
    RPM_MAP.put(4.92660358, -3500.0);
  }

  static {
    INTERPOLATION_MAP.put(2.03329389, 0.0);
    INTERPOLATION_MAP.put(2.59498157, 0.0);
    INTERPOLATION_MAP.put(2.80519751, 0.0);
    INTERPOLATION_MAP.put(3.79530137, -12.45);
    INTERPOLATION_MAP.put(4.39056989, -13.0);
    INTERPOLATION_MAP.put(4.92660358, -14.0);
  }
}
