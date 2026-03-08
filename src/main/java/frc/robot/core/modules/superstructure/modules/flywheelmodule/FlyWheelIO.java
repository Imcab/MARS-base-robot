// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.flywheelmodule;

import com.stzteam.features.marsprocessor.Fallback;
import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO.FlyWheelInputs;

@Fallback
public interface FlyWheelIO extends IO<FlyWheelInputs> {

  public static class FlyWheelInputs extends Data<FlyWheelInputs> {

    @Unit(value = "Volts", group = "FlyWheel")
    public double appliedVolts = 0;

    @Unit(value = "RPM", group = "FlyWheel")
    public double targetRPM = 0;

    @Unit(value = "RPM", group = "FlyWheel")
    public double velocityRPM = 0;
  }

  public void applyOutput(@Unit(value = "Volts", group = "FlyWheel") double volts);

  public void setSpeed(@Unit(value = "DutyCycle", group = "FlyWheel") double speed);

  public void setTargetRPM(@Unit(value = "RPM", group = "FlyWheel") double rpm);
}
