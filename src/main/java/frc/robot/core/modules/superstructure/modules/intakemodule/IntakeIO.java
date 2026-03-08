// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.intakemodule;

import com.stzteam.features.marsprocessor.Fallback;
import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;

@Fallback
public interface IntakeIO extends IO<IntakeIO.IntakeInputs> {

  public static class IntakeInputs extends Data<IntakeInputs> {

    @Unit(value = "Degrees", group = "Intake")
    public double position = 0;

    @Unit(value = "Degrees", group = "Intake")
    public double targetAngle = 0;

    @Unit(value = "Volts", group = "Intake")
    public double appliedVolts = 0;
  }

  public void setPosition(@Unit(value = "Degrees", group = "Intake") double Angle, intakeMODE mode);

  public void applyOutput(@Unit(value = "Volts", group = "Intake") double volts);

  public void resetPosition();

  public void stopAll();
}
