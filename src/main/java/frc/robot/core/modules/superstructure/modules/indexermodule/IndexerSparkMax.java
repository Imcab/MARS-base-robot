// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.indexermodule;

import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import frc.robot.configuration.constants.ModuleConstants.IndexerConstants;

public class IndexerSparkMax implements IndexerIO {

  private final SparkMax rollers;
  private final RelativeEncoder rollersEncoder;

  private final SparkMax indexMotor;
  private final RelativeEncoder indexEncoder;

  public IndexerSparkMax() {
    rollers = new SparkMax(IndexerConstants.ROLLERS_MOTOR_CAN_ID, MotorType.kBrushless);
    rollersEncoder = rollers.getEncoder();

    indexMotor = new SparkMax(IndexerConstants.INDEX_MOTOR_CAN_ID, MotorType.kBrushless);
    indexEncoder = indexMotor.getEncoder();

    configureMotor();
  }

  public void configureMotor() {
    var config = new SparkMaxConfig();

    rollers.setCANTimeout(250);
    indexMotor.setCANTimeout(250);

    config
        .idleMode(IdleMode.kBrake)
        .inverted(IndexerConstants.kMotorInverted)
        .smartCurrentLimit(IndexerConstants.SmartCurrentLimit)
        .voltageCompensation(IndexerConstants.VoltageCompesation);

    config.absoluteEncoder.inverted(IndexerConstants.kEncoderInverted);

    optimizeCANBus(config);

    rollers.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    indexMotor.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    rollers.setCANTimeout(0);
    indexMotor.setCANTimeout(0);
  }

  public void optimizeCANBus(SparkMaxConfig config) {
    config
        .signals
        .primaryEncoderPositionPeriodMs(500)
        .primaryEncoderVelocityPeriodMs(500)
        .analogPositionPeriodMs(500)
        .analogVelocityPeriodMs(500)
        .absoluteEncoderPositionPeriodMs(500);
  }

  @Override
  public void updateInputs(IndexerInputs inputs) {
    inputs.appliedVoltsRoll = rollers.getAppliedOutput() * rollers.getBusVoltage();
    inputs.velocityRoll = rollersEncoder.getVelocity();

    inputs.appliedVoltsIndex = indexMotor.getAppliedOutput() * indexMotor.getBusVoltage();
    inputs.velocityRoll = indexEncoder.getVelocity();
  }

  @Override
  public void applyOutput(double rollersVolts, double indexVolts) {
    rollers.setVoltage(rollersVolts);
    indexMotor.setVoltage(indexVolts);
  }

  @Override
  public void setSpeed(double speed) {
    rollers.set(speed);
    indexMotor.set(speed);
  }

  @Override
  public void stopAll() {
    rollers.stopMotor();
    indexMotor.stopMotor();
  }
}
