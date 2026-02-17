package frc.robot.core.modules.superstructure.modules.armmodule;

import edu.wpi.first.math.geometry.Rotation2d;
import mars.source.models.singlemodule.Data;
import mars.source.models.singlemodule.IO;


public interface ArmIO extends IO<ArmIO.ArmInputs>{

    public static class ArmInputs extends Data<ArmInputs>{

        public double position;
        public Rotation2d rotation;
        public double targetAngle;

    }

    public void applyOutput(double volts);

    public void setPosition(double angle);

}    

