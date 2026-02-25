package frc.robot.core.modules.superstructure.modules.flywheelmodule;

import mars.source.models.singlemodule.Data;
import mars.source.models.singlemodule.IO;
import mars.src.processor.Fallback;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO.FlyWheelInputs;

@Fallback
public interface FlyWheelIO extends IO<FlyWheelInputs>{

    public static class FlyWheelInputs extends Data<FlyWheelInputs>{
        public double appliedVolts = 0;
        public double targetRPM = 0;
        public double velocityRPM = 0;
    }

    public void applyOutput(double volts);
    public void setSpeed(double speed);
    public void setTargetRPM(double rpm);
    
}
