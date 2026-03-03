package frc.robot.core.modules.superstructure.modules.flywheelmodule;

import mars.src.processor.Fallback;

import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;

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