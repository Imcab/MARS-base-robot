// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.armmodule;

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
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIO.ArmInputs;
import frc.robot.core.requests.moduleRequests.ArmCommands;
import frc.robot.core.requests.moduleRequests.ArmRequest;
import frc.robot.core.requests.moduleRequests.ArmRequestFactory;
import java.util.function.Supplier;

public class Arm extends ModularSubsystem<ArmIO.ArmInputs, ArmIO> implements ArmCommands {

  public static final ModuleColorCode IDLE =
      ModuleColorCode.solid("IDLE", Severity.OK, Color.kDarkGreen, "Brazo en reposo");
  public static final ModuleColorCode ON_TARGET =
      ModuleColorCode.solid("ON_TARGET", Severity.OK, Color.kFirstBlue, "En objetivo: %.2f°");
  public static final ModuleColorCode MOVING =
      ModuleColorCode.solid("MOVING", Severity.WARNING, Color.kYellow, "Moviendo a %.2f°");
  public static final ModuleColorCode OUT_OF_RANGE =
      ModuleColorCode.solid(
          "OUT_OF_RANGE", Severity.ERROR, Color.kOrange, "Peligro: Fuera de límite");
  public static final ModuleColorCode MANUAL =
      ModuleColorCode.solid("MANUAL", Severity.WARNING, Color.kBrown, "Control Manual: %.2fV");

  public Arm(ArmIO io) {

    super(
        SubsystemBuilder.<ArmInputs, ArmIO>setup()
            .key(KeyManager.ARM_KEY)
            .hardware(io, new ArmInputs())
            .request(ArmRequestFactory.idle())
            .telemetry(new ArmTelemetry()));

    this.setDefaultCommand(runRequest(() -> ArmRequestFactory.idle()));
  }

  @Override
  public Command setControl(Supplier<ArmRequest> request) {
    return runRequest(request);
  }

  @Override
  public void absolutePeriodic(ArmInputs inputs) {}

  @Override
  public ArmInputs getState() {
    return inputs;
  }

  public boolean isAtTarget(double toleranceDegrees) {
    return MathUtil.isNear(inputs.targetAngle, inputs.position, toleranceDegrees);
  }

  public static class ArmTelemetry extends Telemetry<ArmIO.ArmInputs> {

    @Override
    public void telemeterize(ArmInputs data) {

      NetworkIO.set(KeyManager.ARM_KEY, "Angle", data.position);
      NetworkIO.set(KeyManager.ARM_KEY, "TrageAngle", data.targetAngle);
      NetworkIO.set(KeyManager.ARM_KEY, "Current", data.current);
    }
  }
}
