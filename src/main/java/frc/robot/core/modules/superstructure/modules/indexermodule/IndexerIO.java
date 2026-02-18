package frc.robot.core.modules.superstructure.modules.indexermodule;

import mars.source.models.singlemodule.Data;
import mars.source.models.singlemodule.IO;

public interface IndexerIO extends IO<IndexerIO.IndexerInputs> {

    public static class IndexerInputs extends Data<IndexerInputs> {

        public double appliedVoltsRoll;
        public double velocityRoll;

        public double appliedVoltsIndex;
        public double velocityIndex;
        
    }

    public void applyOutput(double volts);
    public void setSpeed(double speed);
    public void stopAll();
    
}
