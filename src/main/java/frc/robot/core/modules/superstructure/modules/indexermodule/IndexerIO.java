package frc.robot.core.modules.superstructure.modules.indexermodule;

import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;
import com.stzteam.features.marsprocessor.Fallback;

@Fallback
public interface IndexerIO extends IO<IndexerIO.IndexerInputs> {

    public static class IndexerInputs extends Data<IndexerInputs> {

        @Unit(value = "Volts", group = "Indexer")
        public double appliedVoltsRoll= 0;
        @Unit(value = "RPM", group = "Indexer")
        public double velocityRoll = 0;
        
        @Unit(value = "Volts", group = "Indexer")
        public double appliedVoltsIndex = 0;
        @Unit(value = "RPM", group = "Indexer")
        public double velocityIndex = 0;
        
    }

    public void applyOutput(@Unit(value = "Volts", group = "Indexer") double volts);
    public void setSpeed(@Unit(value = "DutyCycle", group = "Indexer") double speed);
    public void stopAll();
    
}
