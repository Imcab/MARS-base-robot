// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.intakemodule;

import com.stzteam.features.dictionary.Dictionary.CommonTables;
import com.stzteam.features.dictionary.Dictionary.CommonTables.Terminology;
import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.models.Telemetry;
import com.stzteam.mars.models.singlemodule.ModularSubsystem;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.configuration.KeyManager;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIO.IntakeInputs;
import frc.robot.core.requests.moduleRequests.IntakeCommands;
import frc.robot.core.requests.moduleRequests.IntakeRequest;
import frc.robot.core.requests.moduleRequests.IntakeRequestFactory;
import java.util.function.Supplier;

public class Intake extends ModularSubsystem<IntakeInputs, IntakeIO> implements IntakeCommands {

  public Intake(IntakeIO io) {

    super(
        SubsystemBuilder.<IntakeInputs, IntakeIO>setup()
            .key(KeyManager.INTAKE_KEY)
            .hardware(io, new IntakeInputs())
            .request(IntakeRequestFactory.idle())
            .telemetry(new IntakeTelemetry()));

    registerTelemetry(new IntakeTelemetry());
    this.setDefaultCommand(runRequest(() -> IntakeRequestFactory.idle()));
  }

  @Override
  public IntakeInputs getState() {
    return inputs;
  }

  public boolean isAtTarget(double toleranceDegrees) {
    return MathUtil.isNear(inputs.targetAngle, inputs.position, toleranceDegrees);
  }

  @Override
  public void absolutePeriodic(IntakeInputs inputs) {}

  @Override
  public Command setControl(Supplier<IntakeRequest> request) {
    return runRequest(request);
  }

  public static class IntakeTelemetry extends Telemetry<IntakeInputs> {

    @Override
    public void telemeterize(IntakeInputs data, ActionStatus lastStatus) {
      NetworkIO.set(KeyManager.INTAKE_KEY, CommonTables.DEGREES_KEY, data.position);
      NetworkIO.set(KeyManager.INTAKE_KEY, CommonTables.TARGET_KEY, data.targetAngle);
      NetworkIO.set(KeyManager.INTAKE_KEY, CommonTables.TIMESTAMP_KEY, data.timestamp);
      NetworkIO.set(
          KeyManager.INTAKE_KEY, CommonTables.APPLIED_KEY + Terminology.VOLTS, data.appliedVolts);

      if (lastStatus != null && lastStatus.code != null) {
        NetworkIO.set(KeyManager.INTAKE_KEY, CommonTables.PAYLOAD_NAME_KEY, KeyManager.INTAKE_KEY);
        NetworkIO.set(
            KeyManager.INTAKE_KEY,
            CommonTables.PAYLOAD_HEX_KEY,
            lastStatus.getPayload().colorHex());
        NetworkIO.set(
            KeyManager.INTAKE_KEY,
            CommonTables.PAYLOAD_MESSAGE_KEY,
            lastStatus.getPayload().message());
      }
    }
  }
}
