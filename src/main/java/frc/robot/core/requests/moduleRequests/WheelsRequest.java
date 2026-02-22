package frc.robot.core.requests.moduleRequests;

import frc.robot.configuration.KeyManager.StatusCodes;

import frc.robot.core.modules.superstructure.modules.wheelsmodule.WheelsIO;
import frc.robot.core.modules.superstructure.modules.wheelsmodule.WheelsIO.WheelsInputs;

import frc.robot.diagnostics.WheelsCode;
import mars.source.diagnostics.ActionStatus;
import mars.source.requests.Request;

public interface WheelsRequest extends Request<WheelsInputs, WheelsIO>{

    public static class Idle implements WheelsRequest {
        @Override
        public ActionStatus apply(WheelsInputs parameters, WheelsIO actor) {
            actor.applyOutput(0);
            return ActionStatus.of(WheelsCode.IDLE, StatusCodes.IDLE_STATUS);
        }
    }

    public static class moveVoltage implements WheelsRequest {
        
        double volts;

        public moveVoltage withVolts(double target){
            this.volts = target;
            return this;
        }
        
        @Override
        public ActionStatus apply(WheelsInputs parameters, WheelsIO actor) {
            actor.applyOutput(volts);
            return ActionStatus.of(WheelsCode.VOLTAGE, StatusCodes.voltsOf(volts));
        }
    }
    
}
