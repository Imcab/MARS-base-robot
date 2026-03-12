// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.turretmodule;

import com.stzteam.features.marsprocessor.Fallback;
import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO.TurretInputs;

@Fallback
public interface TurretIO extends IO<TurretInputs> {

  public static class TurretInputs extends Data<TurretInputs> {

    @Unit(value = "Rotations", group = "Turret")
    public Rotation2d angle = new Rotation2d();

    @Unit(value = "Rotations", group = "Turret")
    public Rotation2d targetAngle = new Rotation2d();

    @Unit(value = "RPS", group = "Turret")
    public double velocityRPS = 0;

    @Unit(value = "Volts", group = "Turret")
    public double appliedVolts = 0.0;

    public Pose2d robotPose = new Pose2d();
    public ChassisSpeeds robotSpeed = new ChassisSpeeds();

    @Override
    public TurretInputs snapshot() {
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

  public void setVoltage(@Unit(value = "Volts", group = "Turret") double volts);

  public void setPosition(@Unit(value = "Rotations", group = "Turret") Rotation2d angle);

  public void setSpeed(@Unit(value = "DutyCycle", group = "Turret") double speed);

  public void setPositionWithFF(
      @Unit(value = "Rotations", group = "Turret") Rotation2d angle,
      @Unit(value = "Volts", group = "Turret") double arbFFVolts);

  public void stop();

  public void resetEnc();
}
