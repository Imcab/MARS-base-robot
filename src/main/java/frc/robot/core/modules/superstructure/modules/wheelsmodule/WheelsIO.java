package frc.robot.core.modules.superstructure.modules.wheelsmodule;

import frc.robot.core.modules.superstructure.modules.wheelsmodule.WheelsIO.WheelsInputs;
import mars.source.models.singlemodule.Data;
import mars.source.models.singlemodule.IO;

public interface WheelsIO extends IO<WheelsInputs> {

    public static class WheelsInputs extends Data<WheelsInputs>{
        public double appliedVolts;
        public double velocityRPM;   
    }
    
    public void applyOutput(double volts);
    public void stop();

}
