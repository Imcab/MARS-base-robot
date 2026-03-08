// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.constants.ModuleConstants;

import com.ctre.phoenix6.signals.InvertedValue;
import edu.wpi.first.math.util.Units;

public class IntakeConstants {

  public static final int Angulator_MOTOR_CAN_ID = 13;
  public static final int currentLimit = 80;

  public static final InvertedValue invertedValue = InvertedValue.CounterClockwise_Positive;

  public static final double kCruiseVelocity = 42;
  public static final double kMaxAcc = 80;

  public static final double kGearRatio = 20.0;

  public static final double kIntakeLengthMeters = 0.4; // 30 cm de largo
  public static final double kIntakeMassKg = 6.7; // 3 kg de peso

  public static final double kMinAngleRads = Units.degreesToRadians(-150);
  public static final double kMaxAngleRads = Units.degreesToRadians(-10);
}
