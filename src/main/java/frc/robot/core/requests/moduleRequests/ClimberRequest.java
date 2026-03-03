package frc.robot.core.requests.moduleRequests;

import frc.robot.core.modules.superstructure.modules.climbermodule.ClimberIO.ClimberInputs;
import frc.robot.diagnostics.ClimberCode;
import mars.src.processor.RequestFactory;
import frc.robot.core.modules.superstructure.modules.climbermodule.ClimberIO;

import com.stzteam.features.dictionary.Dictionary.StatusCodes;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.requests.Request;

@RequestFactory
public interface ClimberRequest extends Request <ClimberInputs, ClimberIO> {
    
    public static class Idle implements ClimberRequest {
        @Override
        public ActionStatus apply(ClimberInputs parameters, ClimberIO actor) {
            actor.applyOutput(0);
            return ActionStatus.of(ClimberCode.IDLE, StatusCodes.IDLE_STATUS);
        }
    }

    public static class moveVoltage implements ClimberRequest {
        
        private double volts;

        public moveVoltage withVolts(double target){
            this.volts = target;
            return this;
        }
        
        @Override
        public ActionStatus apply(ClimberInputs parameters, ClimberIO actor) {
            actor.applyOutput(volts);
            return ActionStatus.of(ClimberCode.VOLTAGE, StatusCodes.voltsOf(volts));
        }
    }//skibidi boiler


}
