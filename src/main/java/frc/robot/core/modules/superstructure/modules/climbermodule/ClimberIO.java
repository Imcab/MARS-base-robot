package frc.robot.core.modules.superstructure.modules.climbermodule;

import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;

import com.stzteam.features.marsprocessor.Fallback;

@Fallback
public interface ClimberIO extends IO<ClimberIO.ClimberInputs>{

    public static class ClimberInputs extends Data<ClimberInputs>{

        public double appliedVolts = 0;
        
    }

    public void applyOutput(@Unit(value = "Volts", group = "Climber") double volts);
    
    public void setSpeed(@Unit(value = "DutyCycle", group = "Climber") double speed);

}
