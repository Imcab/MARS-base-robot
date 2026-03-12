// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.armmodule;

import com.stzteam.features.marsprocessor.Fallback;
import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOKraken.ArmMODE;

@Fallback
public interface ArmIO extends IO<ArmIO.ArmInputs> {

  public static class ArmInputs extends Data<ArmInputs> {

    @Unit(value = "Degrees", group = "Arm")
    public double position = 0;

    @Unit(value = "Radians", group = "Arm")
    public Rotation2d rotation = new Rotation2d();

    @Unit(value = "Degrees", group = "Arm")
    public double targetAngle = 0;
  }

  public void applyOutput(@Unit(value = "Volts", group = "Arm") double volts);

  public void setPosition(@Unit(value = "Degrees", group = "Arm") double angle, ArmMODE mode);

  public void setSpeed(@Unit(value = "DutyCycle", group = "Turret") double speed);
}
