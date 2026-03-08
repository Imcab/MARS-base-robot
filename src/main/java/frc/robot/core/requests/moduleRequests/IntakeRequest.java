// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.requests.moduleRequests;

import com.stzteam.features.dictionary.Dictionary.StatusCodes;
import com.stzteam.features.marsprocessor.CreateCommand;
import com.stzteam.features.marsprocessor.RequestFactory;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.requests.Request;
import edu.wpi.first.math.MathUtil;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIO;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIO.IntakeInputs;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;
import frc.robot.diagnostics.IntakeCode;

@RequestFactory
public interface IntakeRequest extends Request<IntakeInputs, IntakeIO> {

  @CreateCommand(name = "stop")
  public static class Idle implements IntakeRequest {
    @Override
    public ActionStatus apply(IntakeInputs data, IntakeIO actor) {
      actor.stopAll();
      return ActionStatus.of(IntakeCode.IDLE, "Idle");
    }
  }

  @CreateCommand(name = "seed")
  public static class resetPosition implements IntakeRequest {
    @Override
    public ActionStatus apply(IntakeInputs data, IntakeIO actor) {
      actor.resetPosition();
      return ActionStatus.of(IntakeCode.RESET, "Reseted");
    }
  }

  @CreateCommand(name = "toAngle")
  public static class setAngle implements IntakeRequest {
    private double angle;
    private double tolerance = 1.0; // Grados de tolerancia por defecto
    private intakeMODE mode = intakeMODE.kUP;

    public setAngle(double initialAngle) {
      this.angle = initialAngle;
    }

    public setAngle withAngle(double angle) {
      this.angle = angle;
      return this;
    }

    public setAngle withMode(intakeMODE mode) {
      this.mode = mode;
      return this;
    }

    public setAngle Tolerance(double tolerance) {
      this.tolerance = tolerance;
      return this;
    }

    @Override
    public ActionStatus apply(IntakeInputs parameters, IntakeIO actor) {
      parameters.targetAngle = angle;
      actor.setPosition(angle, mode);

      boolean isAtTarget = MathUtil.isNear(angle, parameters.position, tolerance);

      if (isAtTarget) {
        return ActionStatus.of(IntakeCode.ON_TARGET, StatusCodes.TARGETREACHED_STATUS);
      } else {
        return ActionStatus.of(
            IntakeCode.MOVING_TO_ANGLE, StatusCodes.TARGET_STATUS + StatusCodes.angleOf(angle));
      }
    }
  }

  @CreateCommand(name = "voltageCommand")
  public static class moveVoltage implements IntakeRequest {
    private double voltage;

    public moveVoltage withVolts(double volts) {
      this.voltage = volts;
      return this;
    }

    @Override
    public ActionStatus apply(IntakeInputs parameters, IntakeIO actor) {
      actor.applyOutput(voltage);
      return ActionStatus.of(
          IntakeCode.MANUAL_OVERRIDE, StatusCodes.MANUAL_STATUS + StatusCodes.voltsOf(voltage));
    }
  }
}
