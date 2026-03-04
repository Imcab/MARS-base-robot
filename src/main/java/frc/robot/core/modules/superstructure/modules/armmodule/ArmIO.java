package frc.robot.core.modules.superstructure.modules.armmodule;

import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOKraken.ArmMODE;

import com.stzteam.features.marsprocessor.Fallback;


@Fallback
public interface ArmIO extends IO<ArmIO.ArmInputs>{

    public static class ArmInputs extends Data<ArmInputs>{

        @Unit(value = "Degrees", group = "Arm")
        public double position = 0;
        
        public Rotation2d rotation = new Rotation2d();

        @Unit(value = "Degrees", group = "Arm")
        public double targetAngle = 0;

    }

    public void applyOutput(double volts);

    public void setPosition(double angle, ArmMODE mode);

}    

