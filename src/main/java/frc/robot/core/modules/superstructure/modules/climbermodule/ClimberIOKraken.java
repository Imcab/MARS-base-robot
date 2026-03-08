// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.climbermodule;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import frc.robot.configuration.constants.ModuleConstants.ClimberConstants;
import frc.robot.configuration.constants.ModuleConstants.TunerConstants;

public class ClimberIOKraken implements ClimberIO {

  private final TalonFX climber;
  private TalonFXConfigurator climberConfigurator;

  public ClimberIOKraken() {

    climber = new TalonFX(ClimberConstants.Climber_MOTOR_CAN_ID, TunerConstants.kCANBus);
    climberConfigurator = climber.getConfigurator();

    var motorConfigs = new MotorOutputConfigs();

    motorConfigs.Inverted = ClimberConstants.invertedValue;
    motorConfigs.NeutralMode = NeutralModeValue.Brake;

    var limitConfigs = new CurrentLimitsConfigs();
    limitConfigs.StatorCurrentLimit = ClimberConstants.currentLimit;
    limitConfigs.StatorCurrentLimitEnable = false;

    climber.getConfigurator().apply(limitConfigs);

    climberConfigurator.refresh(motorConfigs);
    climberConfigurator.apply(motorConfigs);
  }

  @Override
  public void setSpeed(double speed) {
    climber.set(speed);
  }

  @Override
  public void updateInputs(ClimberInputs inputs) {

    inputs.appliedVolts = climber.getMotorVoltage().getValueAsDouble();
  }

  @Override
  public void applyOutput(double volts) {
    climber.setVoltage(volts);
  }
}
