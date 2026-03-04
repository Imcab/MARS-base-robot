package frc.robot.core.modules.superstructure.modules.flywheelmodule;

import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;

import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO.FlyWheelInputs;

import com.stzteam.features.marsprocessor.Fallback;

@Fallback
public interface FlyWheelIO extends IO<FlyWheelInputs>{

    public static class FlyWheelInputs extends Data<FlyWheelInputs>{
        public double appliedVolts = 0;
        @Unit(value = "RPM", group = "FlyWheel")
        public double targetRPM = 0;
        @Unit(value = "RPM", group = "FlyWheel")
        public double velocityRPM = 0;
    }

    public void applyOutput(@Unit(value = "Volts", group = "FlyWheel") double volts);
    public void setSpeed(@Unit(value = "DutyCycle", group = "FlyWheel") double speed);
    public void setTargetRPM(@Unit(value = "RPM", group = "FlyWheel") double rpm);
    
}