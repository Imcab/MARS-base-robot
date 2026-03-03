package frc.robot.core.modules.superstructure.modules.climbermodule;

import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;

import mars.src.processor.Fallback;

@Fallback
public interface ClimberIO extends IO<ClimberIO.ClimberInputs>{

    public static class ClimberInputs extends Data<ClimberInputs>{

        public double appliedVolts = 0;
        
    }

    public void applyOutput(@Unit("Volts")double volts);
    
    public void setSpeed(@Unit("DutyCycle") double speed);

    

    




}
