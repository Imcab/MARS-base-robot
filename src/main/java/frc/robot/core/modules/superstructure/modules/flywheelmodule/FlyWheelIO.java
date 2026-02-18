package frc.robot.core.modules.superstructure.modules.flywheelmodule;

import mars.source.models.singlemodule.Data;
import mars.source.models.singlemodule.IO;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO.FlyWheelInputs;

public interface FlyWheelIO extends IO<FlyWheelInputs>{

    public static class FlyWheelInputs extends Data<FlyWheelInputs>{

        public double appliedVolts;
        public double targetRPM;
        public double velocityRPM;
        
    }

    public void applyOutput(double volts);

    public void setTargetRPM(double rpm);
    
}
