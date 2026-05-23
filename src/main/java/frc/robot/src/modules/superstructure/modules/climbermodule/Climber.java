// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.src.modules.superstructure.modules.climbermodule;

import com.stzteam.features.dictionary.Dictionary.CommonTables;
import com.stzteam.features.dictionary.Dictionary.CommonTables.Terminology;
import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.mars.diagnostics.ModuleColorCode;
import com.stzteam.mars.diagnostics.StatusColorCode.Severity;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.models.Telemetry;
import com.stzteam.mars.models.singlemodule.ModularSubsystem;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.configuration.KeyManager;
import frc.robot.src.modules.superstructure.modules.climbermodule.ClimberIO.ClimberInputs;
import frc.robot.src.requests.moduleRequests.ClimberCommands;
import frc.robot.src.requests.moduleRequests.ClimberRequest;
import frc.robot.src.requests.moduleRequests.ClimberRequestFactory;
import java.util.function.Supplier;

public class Climber extends ModularSubsystem<ClimberInputs, ClimberIO> implements ClimberCommands {

  public static final ModuleColorCode IDLE =
      ModuleColorCode.solid("IDLE", Severity.OK, Color.kDarkGreen, "Escalador en reposo");
  public static final ModuleColorCode CLIMBING =
      ModuleColorCode.solid("CLIMBING", Severity.WARNING, Color.kYellow, "Escalando");
  public static final ModuleColorCode VOLTAGE =
      ModuleColorCode.solid("VOLTAGE", Severity.OK, Color.kFirstBlue, "Voltaje: %.2fV");
  public static final ModuleColorCode DOWN =
      ModuleColorCode.solid("DOWN", Severity.WARNING, Color.kOrange, "Bajando");

  public Climber(ClimberIO io) {

    super(
        SubsystemBuilder.<ClimberInputs, ClimberIO>setup()
            .key(KeyManager.CLIMBER_KEY)
            .hardware(io, new ClimberInputs())
            .request(ClimberRequestFactory.idle())
            .telemetry(new ClimberTelemetry()));

    this.setDefaultCommand(runRequest(() -> ClimberRequestFactory.idle()));
  }

  @Override
  public Command setControl(Supplier<ClimberRequest> request) {
    return runRequest(request);
  }

  @Override
  public ClimberInputs getState() {
    return inputs;
  }

  public static class ClimberTelemetry extends Telemetry<ClimberInputs> {

    private static final String APPLIED_VOLTS_KEY = CommonTables.APPLIED_KEY + Terminology.VOLTS;

    @Override
    public void telemeterize(ClimberInputs data) {

      NetworkIO.set(KeyManager.CLIMBER_KEY, APPLIED_VOLTS_KEY, data.appliedVolts);
      NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.TIMESTAMP_KEY, data.timestamp);
    }
  }

  @Override
  public void absolutePeriodic(ClimberInputs inputs) {}

  @Override
  public void simulationPeriodic() {}
}
