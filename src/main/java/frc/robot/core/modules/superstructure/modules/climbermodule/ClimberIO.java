package frc.robot.core.modules.superstructure.modules.climbermodule;
import mars.source.models.singlemodule.Data;
import mars.source.models.singlemodule.IO;

public interface ClimberIO extends IO<ClimberIO.ClimberInputs>{

    public static class ClimberInputs extends Data<ClimberInputs>{

        public double appliedVolts;
    
        
    }

    public void applyOutput(double volts);
    
    public void setSpeed(double speed);

    

    




}
