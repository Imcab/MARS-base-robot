// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.intakemodule;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.controls.MotionMagicExpoVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.GravityTypeValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.util.Units;
import frc.robot.configuration.constants.ModuleConstants.IntakeConstants;
import frc.robot.configuration.constants.ModuleConstants.TunerConstants;

public class IntakeIOKraken implements IntakeIO {

  private TalonFX angulator;
  private TalonFXConfigurator angulatorConfigurator;
  private TalonFXConfiguration config;
  private MotionMagicExpoVoltage motionRequest;

  public IntakeIOKraken() {
    angulator = new TalonFX(IntakeConstants.Angulator_MOTOR_CAN_ID, TunerConstants.kCANBus);
    angulatorConfigurator = angulator.getConfigurator();
    config = new TalonFXConfiguration();

    motionRequest = new MotionMagicExpoVoltage(0);

    configMotion();
  }

  public void configMotion() {
    var motorConfigs = new MotorOutputConfigs();

    motorConfigs.Inverted = InvertedValue.CounterClockwise_Positive;
    motorConfigs.NeutralMode = NeutralModeValue.Brake;

    var limitConfigs = new CurrentLimitsConfigs();
    limitConfigs.StatorCurrentLimit = IntakeConstants.currentLimit;
    limitConfigs.StatorCurrentLimitEnable = true;

    config.Feedback.SensorToMechanismRatio = 36.0;
    config.Feedback.RotorToSensorRatio = 1;

    var slot0Configs = config.Slot0;

    slot0Configs.kS = 0.9; // Add 0.25 V output to overcome static friction
    slot0Configs.kV = 4.1; // A velocity target of 1 rps results in 0.12 V output
    slot0Configs.kA = 1; // An acceleration of 1 rps/s requires 0.01 V output
    slot0Configs.kP = 18; // A position error of 2.5 rotations results in 12 V output
    slot0Configs.kI = 0; // no out  put for integrated error
    slot0Configs.kD = 0.05; // A velocity error of 1 rps results in 0.1 V output
    slot0Configs.kG = 0.35;

    slot0Configs.GravityType = GravityTypeValue.Arm_Cosine;

    var slot1Configs = config.Slot1;

    slot1Configs.kS = 0.9; // Add 0.25 V output to overcome sta  tic friction
    slot1Configs.kV = 4.1; // A velocity target of 1 rps results in 0.12 V output
    slot1Configs.kA = 0; // An acceleration of 1 rps/s requires 0.01 V output
    slot1Configs.kP = 15; // A position error of 2.5 rotations results in 12 V output
    slot1Configs.kI = 0.05; // no output for integrated error
    slot1Configs.kD = 0.35; // A velocity error of 1 rps results in 0.1 V output

    var motionMagicConfigs = config.MotionMagic;

    motionMagicConfigs.MotionMagicExpo_kV = 0;
    motionMagicConfigs.MotionMagicExpo_kA = 0;

    angulatorConfigurator.apply(config);
    angulatorConfigurator.apply(limitConfigs);
    angulatorConfigurator.apply(motorConfigs);
  }

  public enum intakeMODE {
    kUP,
    kDOWN
  }

  @Override
  public void setPosition(double angle, intakeMODE mode) {
    switch (mode) {
      case kUP:
        angulator.setControl(
            motionRequest.withPosition(Units.degreesToRotations(angle)).withSlot(0));
        break;

      case kDOWN:
        angulator.setControl(
            motionRequest.withPosition(Units.degreesToRotations(angle)).withSlot(1));
        break;
    }
  }

  @Override
  public void resetPosition() {
    angulator.setPosition(0);
  }

  @Override
  public void applyOutput(double voltage) {
    angulator.setVoltage(voltage);
  }

  @Override
  public void stopAll() {
    angulator.stopMotor();
  }

  @Override
  public void updateInputs(IntakeInputs inputs) {

    var rotorPosSignal = angulator.getPosition();
    var rotorPosRotations = rotorPosSignal.getValueAsDouble();

    inputs.position = Units.rotationsToDegrees(rotorPosRotations);

    inputs.timestamp = rotorPosSignal.getTimestamp().getLatency();
  }
}
