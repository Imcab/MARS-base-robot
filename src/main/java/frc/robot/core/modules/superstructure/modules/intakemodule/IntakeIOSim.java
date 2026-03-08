// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.intakemodule;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import frc.robot.configuration.constants.ModuleConstants.IntakeConstants;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;

public class IntakeIOSim implements IntakeIO {
  private final SingleJointedArmSim simIntake;
  private final ProfiledPIDController simController;

  private double appliedVolts = 0.0;
  private boolean isClosedLoop = false;
  private double currentTargetAngle = 0.0;

  public IntakeIOSim() {

    simIntake =
        new SingleJointedArmSim(
            DCMotor.getNEO(1),
            IntakeConstants.kGearRatio,
            SingleJointedArmSim.estimateMOI(
                IntakeConstants.kIntakeLengthMeters, IntakeConstants.kIntakeMassKg),
            IntakeConstants.kIntakeLengthMeters,
            IntakeConstants.kMinAngleRads,
            IntakeConstants.kMaxAngleRads,
            false,
            Units.degreesToRadians(-30));

    simController =
        new ProfiledPIDController(0.45, 0.0, 0.00, new TrapezoidProfile.Constraints(180.0, 360.0));
  }

  @Override
  public void updateInputs(IntakeInputs inputs) {

    if (isClosedLoop) {
      double currentDegrees = Units.radiansToDegrees(simIntake.getAngleRads());
      appliedVolts = simController.calculate(currentDegrees, currentTargetAngle);
    }

    appliedVolts = MathUtil.clamp(appliedVolts, -12.0, 12.0);
    simIntake.setInputVoltage(appliedVolts);

    simIntake.update(0.02);

    double simulatedDegrees = Units.radiansToDegrees(simIntake.getAngleRads());

    inputs.position = simulatedDegrees;
    inputs.targetAngle = currentTargetAngle;
    inputs.appliedVolts = appliedVolts;
  }

  @Override
  public void applyOutput(double volts) {
    isClosedLoop = false;
    this.appliedVolts = volts;
  }

  @Override
  public void resetPosition() {}

  @Override
  public void setPosition(double angle, intakeMODE mode) {
    isClosedLoop = true;
    this.currentTargetAngle = angle;
  }

  @Override
  public void stopAll() {
    this.appliedVolts = 0;
  }
}
