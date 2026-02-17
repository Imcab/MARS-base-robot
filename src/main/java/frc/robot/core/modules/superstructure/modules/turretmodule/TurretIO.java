package frc.robot.core.modules.superstructure.modules.turretmodule;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import mars.source.models.singlemodule.Data;
import mars.source.models.singlemodule.IO;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO.TurretInputs;

public interface TurretIO extends IO<TurretInputs>{

    public static class TurretInputs extends Data<TurretInputs>{
  
        public Rotation2d angle;
        public Rotation2d targetAngle;
        public double velocityRPS;
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
