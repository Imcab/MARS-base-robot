// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.indexermodule;

import com.stzteam.features.marsprocessor.Fallback;
import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.mars.models.singlemodule.Data;
import com.stzteam.mars.models.singlemodule.IO;

@Fallback
public interface IndexerIO extends IO<IndexerIO.IndexerInputs> {

  public static class IndexerInputs extends Data<IndexerInputs> {

    @Unit(value = "Volts", group = "Indexer")
    public double appliedVoltsRoll = 0;

    @Unit(value = "RPM", group = "Indexer")
    public double velocityRoll = 0;

    @Unit(value = "Volts", group = "Indexer")
    public double appliedVoltsIndex = 0;

    @Unit(value = "RPM", group = "Indexer")
    public double velocityIndex = 0;
  }

  public void applyOutput(
      @Unit(value = "Volts", group = "Indexer") double rollersVolts, double intakeVolts);

  public void setSpeed(@Unit(value = "DutyCycle", group = "Indexer") double speed);

  public void stopAll();
}
