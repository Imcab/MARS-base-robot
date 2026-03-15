// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.flywheelmodule;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import frc.robot.configuration.constants.ModuleConstants.FlywheelConstants.ShooterWheelsConstants;

public class FlyWheelIOKrakenShooter implements FlyWheelIO {

  private TalonFX leaderShooter, followerShooter;
  private TalonFXConfiguration leaderConfig, followerConfig;
  private TalonFXConfigurator leaderConfigurator, followerConfigurator;

  private VelocityVoltage velocityRequest;
  private double velocityTarget;

  public FlyWheelIOKrakenShooter() {
    leaderShooter =
        new TalonFX(
            ShooterWheelsConstants.shooterLeaderID,
            CANBus.roboRIO()); // Si estan en el can de la rico cambiar a "CANBus.roboRIO()"
    followerShooter = new TalonFX(ShooterWheelsConstants.shooterFollowerID, CANBus.roboRIO());

    leaderConfig = new TalonFXConfiguration();
    followerConfig = new TalonFXConfiguration();

    leaderConfigurator = leaderShooter.getConfigurator();
    followerConfigurator = followerShooter.getConfigurator();

    velocityRequest = new VelocityVoltage(0);

    followerShooter.setControl(
        new Follower(ShooterWheelsConstants.shooterLeaderID, MotorAlignmentValue.Opposed));

    configMotor();
  }

  public void configMotor() {
    var limitConfigs = leaderConfig.CurrentLimits;

    limitConfigs.SupplyCurrentLimit = ShooterWheelsConstants.SupplyCurrentLimit;
    limitConfigs.SupplyCurrentLimitEnable = ShooterWheelsConstants.SupplyCurrentLimitEnable;

    limitConfigs.StatorCurrentLimit = ShooterWheelsConstants.StatorCurrentLimit;
    limitConfigs.StatorCurrentLimitEnable = ShooterWheelsConstants.StatorCurrentLimitEnable;

    var slot0Configs = leaderConfig.Slot0;

    slot0Configs.kS = ShooterWheelsConstants.kS;
    slot0Configs.kV = ShooterWheelsConstants.kV;
    slot0Configs.kP = ShooterWheelsConstants.kP;
    slot0Configs.kI = ShooterWheelsConstants.kI;
    slot0Configs.kD = ShooterWheelsConstants.kD;

    leaderConfigurator.apply(leaderConfig);
    followerConfigurator.apply(followerConfig);

    leaderConfigurator.apply(limitConfigs);
    followerConfigurator.apply(limitConfigs);
  }

  @Override
  public void updateInputs(FlyWheelInputs inputs) {
    inputs.velocityRPM =
        leaderShooter.getVelocity().getValueAsDouble()
            * 60.0; // Convert from rotations per second to RPM
    inputs.appliedVolts = leaderShooter.getMotorVoltage().getValueAsDouble();
    inputs.targetRPM = leaderShooter.getPosition().getTimestamp().getLatency();
    inputs.targetRPM = this.velocityTarget;
  }

  @Override
  public void setTargetRPM(double RPM) {
    this.velocityTarget = RPM;
    leaderShooter.setControl(velocityRequest.withVelocity(RPM / 60).withSlot(0));
  }

  @Override
  public void applyOutput(double volts) {
    leaderShooter.setVoltage(volts);
  }

  @Override
  public void setSpeed(double speed) {
    leaderShooter.set(speed);
  }
}
