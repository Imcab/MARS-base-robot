// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.constants.ModuleConstants;

public class IndexerConstants {

  // IDs de los 2 motores, rodillos e index
  public static final int ROLLERS_MOTOR_CAN_ID = 15;
  public static final int INDEX_MOTOR_CAN_ID = 16;

  // Esto es por si el motor gira al revés, en ese caso cambiar a true
  public static final boolean ROLLERS_INVERTED = false;
  public static final boolean INDEX_INVERTED = false;

  public static final double kGearing = 1;
  public static final double kMOI = 0.002;

  public static final boolean kMotorInverted = false;
  public static final boolean kEncoderInverted = false;

  public static final int SmartCurrentLimit = 40;
  public static final double VoltageCompesation = 12;
}
