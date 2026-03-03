package frc.robot.core.modules.superstructure.modules.indexermodule;

import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;

import mars.src.processor.Fallback;

@Fallback
public interface IndexerIO extends IO<IndexerIO.IndexerInputs> {

    public static class IndexerInputs extends Data<IndexerInputs> {

        @Unit("Volts")
        public double appliedVoltsRoll= 0;
        @Unit("RPM")
        public double velocityRoll = 0;

        
        @Unit("Volts")
        public double appliedVoltsIndex = 0;
        @Unit("RPM")
        public double velocityIndex = 0;
        
    }

    public void applyOutput(@Unit("Volts") double volts);
    public void setSpeed(@Unit("DutyCycle") double speed);
    public void stopAll();
    
}
