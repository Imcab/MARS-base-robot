// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.requests.moduleRequests;

import com.stzteam.features.dictionary.Dictionary.StatusCodes;
import com.stzteam.features.marsprocessor.CreateCommand;
import com.stzteam.features.marsprocessor.RequestFactory;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.requests.Request;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerIO;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerIO.IndexerInputs;
import frc.robot.diagnostics.IndexerCode;

@RequestFactory
public interface IndexerRequest extends Request<IndexerInputs, IndexerIO> {

  @CreateCommand(name = "stop")
  public static class Idle implements IndexerRequest {
    @Override
    public ActionStatus apply(IndexerInputs parameters, IndexerIO actor) {
      actor.applyOutput(0, 0);
      return ActionStatus.of(IndexerCode.IDLE, StatusCodes.IDLE_STATUS);
    }
  }

  @CreateCommand(name = "voltageCommand")
  public static class moveVoltage implements IndexerRequest {

    private double rollerVolts = 0;
    private double indexVolts = 0;

    public moveVoltage withRollers(double voltage) {
      this.rollerVolts = voltage;
      return this;
    }

    public moveVoltage withIndex(double voltage) {
      this.indexVolts = voltage;
      return this;
    }

    @Override
    public ActionStatus apply(IndexerInputs parameters, IndexerIO actor) {
      actor.applyOutput(rollerVolts, indexVolts);
      return ActionStatus.of(IndexerCode.VOLTAGE, StatusCodes.voltsOf(rollerVolts));
    }
  }

  /*
  public static class moveSpeed implements IndexerRequest {

      private double volts;

      public moveVoltage withVolts(double target){
          this.volts = target;
          return this;
      }

      @Override
      public ActionStatus apply(IndexerInputs parameters, IndexerIO actor) {
          actor.applyOutput(volts);
          return ActionStatus.of(IndexerCode.VOLTAGE, StatusCodes.voltsOf(volts));
      }
  }*/

}
