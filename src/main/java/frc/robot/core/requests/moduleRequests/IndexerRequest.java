package frc.robot.core.requests.moduleRequests;

import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerIO.IndexerInputs;
import frc.robot.diagnostics.IndexerCode;

import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerIO;

import com.stzteam.features.dictionary.Dictionary.StatusCodes;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.requests.Request;

import com.stzteam.features.marsprocessor.RequestFactory;

@RequestFactory
public interface IndexerRequest extends Request<IndexerInputs, IndexerIO>{

    public static class Idle implements IndexerRequest {
        @Override
        public ActionStatus apply(IndexerInputs parameters, IndexerIO actor) {
            actor.applyOutput(0);
            return ActionStatus.of(IndexerCode.IDLE, StatusCodes.IDLE_STATUS);
        }
    }

    public static class moveVoltage implements IndexerRequest {
        
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
    }


    
}
