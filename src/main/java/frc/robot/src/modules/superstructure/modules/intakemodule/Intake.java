// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.src.modules.superstructure.modules.intakemodule;

import com.stzteam.features.dictionary.Dictionary.CommonTables;
import com.stzteam.features.dictionary.Dictionary.CommonTables.Terminology;
import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.mars.diagnostics.ModuleColorCode;
import com.stzteam.mars.diagnostics.StatusColorCode.Severity;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.models.Telemetry;
import com.stzteam.mars.models.singlemodule.ModularSubsystem;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.configuration.KeyManager;
import frc.robot.src.modules.superstructure.modules.intakemodule.IntakeIO.IntakeInputs;
import frc.robot.src.requests.moduleRequests.IntakeCommands;
import frc.robot.src.requests.moduleRequests.IntakeRequest;
import frc.robot.src.requests.moduleRequests.IntakeRequestFactory;
import java.util.function.Supplier;

public class Intake extends ModularSubsystem<IntakeInputs, IntakeIO> implements IntakeCommands {

  public static final ModuleColorCode IDLE =
      ModuleColorCode.solid("IDLE", Severity.OK, Color.kDarkGreen, "Intake en reposo");
  public static final ModuleColorCode ON_TARGET =
      ModuleColorCode.solid("ON_TARGET", Severity.OK, Color.kFirstBlue, "Intake en objetivo");
  public static final ModuleColorCode MOVING_TO_ANGLE =
      ModuleColorCode.solid(
          "MOVING_TO_ANGLE", Severity.WARNING, Color.kYellow, "Intake moviéndose a %.2f grados");
  public static final ModuleColorCode MANUAL_OVERRIDE =
      ModuleColorCode.solid(
          "MANUAL_OVERRIDE", Severity.WARNING, Color.kPurple, "Intake en control manual");
  public static final ModuleColorCode RESET =
      ModuleColorCode.solid("RESET", Severity.OK, Color.kDarkSalmon, "Intake reiniciado");
  public static final ModuleColorCode OUT_OF_RANGE =
      ModuleColorCode.solid("OUT_OF_RANGE", Severity.ERROR, Color.kOrange, "Intake fuera de rango");

  public Intake(IntakeIO io) {

    super(
        SubsystemBuilder.<IntakeInputs, IntakeIO>setup()
            .key(KeyManager.INTAKE_KEY)
            .hardware(io, new IntakeInputs())
            .request(IntakeRequestFactory.idle())
            .telemetry(new IntakeTelemetry()));

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

    private static final String APPLIED_VOLTS_KEY = CommonTables.APPLIED_KEY + Terminology.VOLTS;

    @Override
    public void telemeterize(IntakeInputs data) {

      NetworkIO.set(KeyManager.INTAKE_KEY, CommonTables.DEGREES_KEY, data.position);
      NetworkIO.set(KeyManager.INTAKE_KEY, CommonTables.TARGET_KEY, data.targetAngle);
      NetworkIO.set(KeyManager.INTAKE_KEY, CommonTables.TIMESTAMP_KEY, data.timestamp);
      NetworkIO.set(KeyManager.INTAKE_KEY, APPLIED_VOLTS_KEY, data.appliedVolts);

      NetworkIO.set(KeyManager.INTAKE_KEY, "Current", data.current);
    }
  }

  @Override
  public void simulationPeriodic() {}
}
