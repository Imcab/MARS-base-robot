// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.requests.moduleRequests;

import com.stzteam.features.dictionary.Dictionary.StatusCodes;
import com.stzteam.features.marsprocessor.CreateCommand;
import com.stzteam.features.marsprocessor.RequestFactory;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.requests.Request;
import frc.robot.core.modules.superstructure.modules.climbermodule.ClimberIO;
import frc.robot.core.modules.superstructure.modules.climbermodule.ClimberIO.ClimberInputs;
import frc.robot.diagnostics.ClimberCode;

@RequestFactory
public interface ClimberRequest extends Request<ClimberInputs, ClimberIO> {

  @CreateCommand(name = "stop")
  public static class Idle implements ClimberRequest {
    @Override
    public ActionStatus apply(ClimberInputs parameters, ClimberIO actor) {
      actor.applyOutput(0);
      return ActionStatus.of(ClimberCode.IDLE, StatusCodes.IDLE_STATUS);
    }
  }

  @CreateCommand(name = "voltageCommand")
  public static class moveVoltage implements ClimberRequest {

    private double volts;

    public moveVoltage withVolts(double target) {
      this.volts = target;
      return this;
    }

    @Override
    public ActionStatus apply(ClimberInputs parameters, ClimberIO actor) {
      actor.applyOutput(volts);
      return ActionStatus.of(ClimberCode.VOLTAGE, StatusCodes.voltsOf(volts));
    }
  } // skibidi boiler
}
