// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.flywheelmodule;

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
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO.FlyWheelInputs;
import frc.robot.core.requests.moduleRequests.FlyWheelCommands;
import frc.robot.core.requests.moduleRequests.FlyWheelRequest;
import frc.robot.core.requests.moduleRequests.FlyWheelRequestFactory;
import java.util.function.Supplier;

public class FlyWheel extends ModularSubsystem<FlyWheelInputs, FlyWheelIO>
    implements FlyWheelCommands {

  public String subKey;

  public enum idleMode {
    intakeIDLE,
    outakeIDLE
  }

  public idleMode mode;

  public static final ModuleColorCode IDLE =
      ModuleColorCode.solid("IDLE", Severity.OK, Color.kDarkGreen, "Flywheel en reposo");
  public static final ModuleColorCode ON_TARGET =
      ModuleColorCode.solid("ON_TARGET", Severity.OK, Color.kFirstBlue, "En objetivo: %.2f RPM");
  public static final ModuleColorCode MOVING_TO_RPM =
      ModuleColorCode.solid(
          "MOVING_TO_RPM", Severity.WARNING, Color.kYellow, "Moviendo a %.2f RPM");
  public static final ModuleColorCode MANUAL_OVERRIDE =
      ModuleColorCode.solid(
          "MANUAL_OVERRIDE", Severity.WARNING, Color.kPurple, "Flywheel en control manual");
  public static final ModuleColorCode MANUAL_CONTROL =
      ModuleColorCode.solid(
          "MANUAL_CONTROL", Severity.WARNING, Color.kBrown, "Control manual: %.2fV");

  public FlyWheel(FlyWheelIO io, String key, idleMode mode) {
    super(
        SubsystemBuilder.<FlyWheelInputs, FlyWheelIO>setup()
            .key(key)
            .hardware(io, new FlyWheelInputs())
            .request(FlyWheelRequestFactory.idleIntake())
            .telemetry(new FlyWheelTelemetry(key)));

    this.mode = mode;
    this.subKey = key;

    if (mode == idleMode.intakeIDLE) {
      this.setDefaultCommand(runRequest(() -> FlyWheelRequestFactory.idleIntake()));
    } else {
      this.setDefaultCommand(runRequest(() -> FlyWheelRequestFactory.idleOutake()));
    }
  }

  @Override
  public FlyWheelInputs getState() {
    return inputs;
  }

  @Override
  public Command setControl(Supplier<FlyWheelRequest> request) {
    return runRequest(request);
  }

  public boolean isAtTarget(double toleranceRPM) {
    return MathUtil.isNear(inputs.targetRPM, inputs.velocityRPM, toleranceRPM);
  }

  public static class FlyWheelTelemetry extends Telemetry<FlyWheelInputs> {

    private static final String VELOCITY_RPM_KEY = CommonTables.VELOCITY_KEY + Terminology.RPM;
    private static final String APPLIED_VOLTS_KEY = CommonTables.APPLIED_KEY + Terminology.VOLTS;
    private static final String TARGET_RPM_KEY = CommonTables.TARGET_KEY + Terminology.RPM;

    String key;

    public FlyWheelTelemetry(String key) {
      this.key = key;
    }

    @Override
    public void telemeterize(FlyWheelInputs data) {

      NetworkIO.set(key, VELOCITY_RPM_KEY, data.velocityRPM);
      NetworkIO.set(key, APPLIED_VOLTS_KEY, data.appliedVolts);
      NetworkIO.set(key, TARGET_RPM_KEY, data.targetRPM);

      NetworkIO.set(key, "Current", data.current);
    }
  }

  @Override
  public void absolutePeriodic(FlyWheelInputs inputs) {}

  @Override
  public void simulationPeriodic() {}
}
