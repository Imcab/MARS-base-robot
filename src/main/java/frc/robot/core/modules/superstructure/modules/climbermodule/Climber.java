// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.climbermodule;

import com.stzteam.features.dictionary.Dictionary.CommonTables;
import com.stzteam.features.dictionary.Dictionary.CommonTables.Terminology;
import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.models.Telemetry;
import com.stzteam.mars.models.singlemodule.ModularSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.configuration.KeyManager;
import frc.robot.core.modules.superstructure.modules.climbermodule.ClimberIO.ClimberInputs;
import frc.robot.core.requests.moduleRequests.ClimberCommands;
import frc.robot.core.requests.moduleRequests.ClimberRequest;
import frc.robot.core.requests.moduleRequests.ClimberRequestFactory;
import java.util.function.Supplier;

public class Climber extends ModularSubsystem<ClimberInputs, ClimberIO> implements ClimberCommands {

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

    private String lastSentHex = "";
    private String lastSentMessage = "";

    @Override
    public void telemeterize(ClimberInputs data, ActionStatus lastStatus) {

      NetworkIO.set(KeyManager.CLIMBER_KEY, APPLIED_VOLTS_KEY, data.appliedVolts);
      NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.TIMESTAMP_KEY, data.timestamp);

      if (lastStatus != null && lastStatus.code != null) {
        String currentHex = lastStatus.getPayload().colorHex();
        String currentMessage = lastStatus.getPayload().message();

        if (!currentHex.equals(lastSentHex) || !currentMessage.equals(lastSentMessage)) {

          NetworkIO.set(
              KeyManager.CLIMBER_KEY, CommonTables.PAYLOAD_NAME_KEY, KeyManager.CLIMBER_KEY);
          NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.PAYLOAD_HEX_KEY, currentHex);
          NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.PAYLOAD_MESSAGE_KEY, currentMessage);

          lastSentHex = currentHex;
          lastSentMessage = currentMessage;
        }
      }
    }
  }

  @Override
  public void absolutePeriodic(ClimberInputs inputs) {}
}
