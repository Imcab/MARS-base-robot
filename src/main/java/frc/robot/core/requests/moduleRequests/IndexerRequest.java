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
            actor.applyOutput(0 ,0);
            return ActionStatus.of(IndexerCode.IDLE, StatusCodes.IDLE_STATUS);
        }
    }

    public static class moveVoltage implements IndexerRequest {
        
        private double rollerVolts = 0;
        private double indexVolts = 0;

        public moveVoltage withRollers(double voltage){
            this.rollerVolts = voltage;
            return this;
        }

        public moveVoltage withIndex(double voltage){
            this.indexVolts = voltage;
            return this;
        }



        @Override
        public ActionStatus apply(IndexerInputs parameters, IndexerIO actor) {
            actor.applyOutput(rollerVolts,indexVolts );
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
