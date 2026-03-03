package frc.robot.core.modules.superstructure.modules.turretmodule;

import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

import mars.src.processor.Fallback;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO.TurretInputs;

@Fallback
public interface TurretIO extends IO<TurretInputs>{

    public static class TurretInputs extends Data<TurretInputs>{
  
        public Rotation2d angle = new Rotation2d();
        public Rotation2d targetAngle = new Rotation2d();
        public double velocityRPS = 0;
        public double appliedVolts = 0.0;

        public Pose2d robotPose = new Pose2d();
        public ChassisSpeeds robotSpeed = new ChassisSpeeds();

        @Override
        public TurretInputs snapshot(){
            TurretInputs clone = new TurretInputs();
            clone.angle = this.angle;
            clone.targetAngle = this.targetAngle;
            clone.velocityRPS = this.velocityRPS;
            clone.timestamp = this.timestamp;
            clone.appliedVolts = this.appliedVolts;
            clone.robotPose = this.robotPose;
            clone.robotSpeed = this.robotSpeed;
            return clone;
        }
    }

    public void setVoltage(double volts);
    public void setPosition(Rotation2d angle);
    public void stop();

}
