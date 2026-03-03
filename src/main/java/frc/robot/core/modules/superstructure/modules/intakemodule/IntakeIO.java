package frc.robot.core.modules.superstructure.modules.intakemodule;

import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;

import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;

import mars.src.processor.Fallback;

@Fallback
public interface IntakeIO extends IO<IntakeIO.IntakeInputs>{

    public static class IntakeInputs extends Data<IntakeInputs>{

        @Unit("Degrees")
        public double position = 0;
        @Unit("Degrees")
        public double targetAngle = 0;
        public double appliedVolts = 0;

    }

    public void setPosition(@Unit("Degrees") double Angle, intakeMODE mode);
    public void applyOutput(double volts);
    public void resetPosition();
    public void stopAll();
 
}
