// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.climbermodule;

import com.stzteam.features.marsprocessor.Fallback;
import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;

@Fallback
public interface ClimberIO extends IO<ClimberIO.ClimberInputs> {

  public static class ClimberInputs extends Data<ClimberInputs> {

    @Unit(value = "Volts", group = "Climber")
    public double appliedVolts = 0;
  }

  public void applyOutput(@Unit(value = "Volts", group = "Climber") double volts);

  public void setSpeed(@Unit(value = "DutyCycle", group = "Climber") double speed);
}
