package frc.robot.core.modules.superstructure.modules.intakemodule;

import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;
import mars.source.models.singlemodule.Data;
import mars.source.models.singlemodule.IO;

public interface IntakeIO extends IO<IntakeIO.IntakeInputs>{

    public static class IntakeInputs extends Data<IntakeInputs>{
        public double position;
        public double targetAngle;
    }

    public void setPosition(double Angle, intakeMODE mode);
    public void applyOutput(double volts);
    public void stopAll();


 
}
