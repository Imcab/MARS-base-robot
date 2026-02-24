package frc.robot.core.modules.superstructure.modules.indexermodule;

import mars.source.models.singlemodule.Data;
import mars.source.models.singlemodule.IO;
import mars.src.processor.Fallback;

@Fallback
public interface IndexerIO extends IO<IndexerIO.IndexerInputs> {

    public static class IndexerInputs extends Data<IndexerInputs> {

        public double appliedVoltsRoll= 0;
        public double velocityRoll = 0;

        public double appliedVoltsIndex = 0;
        public double velocityIndex = 0;
        
    }

    public void applyOutput(double volts);
    public void setSpeed(double speed);
    public void stopAll();
    
}
