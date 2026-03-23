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
  public static final double FLYWHEEL_TOLERANCE = 34.72;
  public static final double ARM_TOLERANCE = 2;

  public static final double kSimLoopPeriod = 0.004;

  public static final Rotation2d kBlueAlliancePerspectiveRotation = Rotation2d.kZero;
  public static final Rotation2d kRedAlliancePerspectiveRotation = Rotation2d.k180deg;

  public static final Translation3d HUB_LOCATION = // TODO: HUB ACTUAL - ROJO
      new Translation3d(12.038, 4.10, 1.9); // TODO: x Azul - 4.630 ---  x Rojo - 12.038

  public static final InterpolatingDoubleTreeMap INTERPOLATION_MAP =
      new InterpolatingDoubleTreeMap();

  public static final InterpolatingDoubleTreeMap RPM_MAP = new InterpolatingDoubleTreeMap();

  static {
    RPM_MAP.put(1.81598, -2800.0);
    RPM_MAP.put(2.29161, -3000.0);
    RPM_MAP.put(2.57937, -3180.0);
    RPM_MAP.put(2.962915, -3400.0);
    RPM_MAP.put(3.466091, -3530.0);
    RPM_MAP.put(4.040353, -3790.0);
    RPM_MAP.put(4.547862, -4130.0);
    RPM_MAP.put(4.902743, -4530.0);
    RPM_MAP.put(5.340625, -4980.0);
  }

  static {
    INTERPOLATION_MAP.put(1.81598, -16.0);
    INTERPOLATION_MAP.put(2.29161, -17.5);
    INTERPOLATION_MAP.put(2.57937, -20.0);
    INTERPOLATION_MAP.put(2.962915, -22.0);
    INTERPOLATION_MAP.put(3.466091, -24.0);
    INTERPOLATION_MAP.put(4.040353, -26.85);
    INTERPOLATION_MAP.put(4.547862, -28.5);
    INTERPOLATION_MAP.put(4.902743, -31.7);
    INTERPOLATION_MAP.put(5.340625, -32.5);
  }
}
