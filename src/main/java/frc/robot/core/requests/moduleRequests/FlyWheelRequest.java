// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.requests.moduleRequests;

import com.stzteam.features.dictionary.Dictionary.CommonTables.Terminology;
import com.stzteam.features.dictionary.Dictionary.StatusCodes;
import com.stzteam.features.marsprocessor.CreateCommand;
import com.stzteam.features.marsprocessor.RequestFactory;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.requests.Request;
import edu.wpi.first.math.MathUtil;
import frc.robot.configuration.constants.Constants;
import frc.robot.configuration.constants.ModuleConstants.FlywheelConstants;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO.FlyWheelInputs;
import frc.robot.diagnostics.FlywheelsCode;
import java.util.function.DoubleSupplier;

@RequestFactory
public interface FlyWheelRequest extends Request<FlyWheelInputs, FlyWheelIO> {

  public static class IdleIntake implements FlyWheelRequest {

    @Override
    public ActionStatus apply(FlyWheelInputs parameters, FlyWheelIO actor) {
      actor.applyOutput(0);
      return ActionStatus.of(FlywheelsCode.IDLE, StatusCodes.IDLE_STATUS);
    }
  }

  public static class IdleOutake implements FlyWheelRequest {

    @Override
    public ActionStatus apply(FlyWheelInputs parameters, FlyWheelIO actor) {
      actor.applyOutput(FlywheelConstants.ShooterWheelsConstants.idleVoltage);
      return ActionStatus.of(FlywheelsCode.IDLE, StatusCodes.IDLE_STATUS);
    }
  }

  @CreateCommand(name = "manual")
  public static class manualShoot implements FlyWheelRequest {
    private DoubleSupplier stick;

    public manualShoot getStick(DoubleSupplier stick) {
      this.stick = stick;
      return this;
    }

    @Override
    public ActionStatus apply(FlyWheelInputs parameters, FlyWheelIO actor) {
      actor.setSpeed(stick.getAsDouble());
      return ActionStatus.of(FlywheelsCode.MANUAL_Control, "Manual");
    }
  }

  @CreateCommand(name = "dutyCycle")
  public static class moveSpeed implements FlyWheelRequest {
    double speed;

    public moveSpeed withSpeed(double speed) {
      this.speed = speed;
      return this;
    }

    @Override
    public ActionStatus apply(FlyWheelInputs parameters, FlyWheelIO actor) {
      actor.setSpeed(speed);
      return ActionStatus.of(FlywheelsCode.MANUAL_OVERRIDE, "Speed");
    }
  }

  @CreateCommand(name = "spinAtVoltage")
  public static class moveVoltage implements FlyWheelRequest {
    double volts;

    public moveVoltage withVolts(double target) {
      this.volts = target;
      return this;
    }

    @Override
    public ActionStatus apply(FlyWheelInputs parameters, FlyWheelIO actor) {
      actor.applyOutput(volts);
      return ActionStatus.of(
          FlywheelsCode.MANUAL_OVERRIDE, StatusCodes.MANUAL_STATUS + StatusCodes.voltsOf(volts));
    }
  }

  @CreateCommand(name = "toRPM")
  public static class SetRPM implements FlyWheelRequest {
    private double rpm;
    private double tolerance = 1.0; // Grados de tolerancia por defecto

    public SetRPM(double rpm) {
      this.rpm = rpm;
    }

    public SetRPM toRPM(double rpm) {
      this.rpm = rpm;
      return this;
    }

    public SetRPM withTolerance(double tol) {
      this.tolerance = tol;
      return this;
    }

    @Override
    public ActionStatus apply(FlyWheelInputs parameters, FlyWheelIO actor) {
      parameters.targetRPM = rpm;
      actor.setTargetRPM(rpm);

      boolean isAtTarget = MathUtil.isNear(rpm, parameters.velocityRPM, tolerance);

      if (isAtTarget) {
        return ActionStatus.of(FlywheelsCode.ON_TARGET, StatusCodes.TARGETREACHED_STATUS);
      } else {
        return ActionStatus.of(
            FlywheelsCode.MOVING_TO_RPM, StatusCodes.TARGET_STATUS + rpm + Terminology.RPM);
      }
    }
  }

  @CreateCommand(name = "distanceToRPM")
  public static class InterpolateRPM implements FlyWheelRequest {
    private DoubleSupplier distanceMetersSupplier;
    private double toleranceRPM = 50.0;

    public InterpolateRPM withDistance(DoubleSupplier distanceSupplier) {
      this.distanceMetersSupplier = distanceSupplier;
      return this;
    }

    public InterpolateRPM withTolerance(double tol) {
      this.toleranceRPM = tol;
      return this;
    }

    @Override
    public ActionStatus apply(FlyWheelInputs data, FlyWheelIO actor) {

      double distance = distanceMetersSupplier.getAsDouble();

      double targetRPM = Constants.RPM_MAP.get(distance);

      data.targetRPM = targetRPM;
      actor.setTargetRPM(targetRPM);

      boolean isAtTarget = MathUtil.isNear(targetRPM, data.velocityRPM, toleranceRPM);

      if (isAtTarget) {
        return ActionStatus.of(FlywheelsCode.ON_TARGET, StatusCodes.TARGETREACHED_STATUS);
      } else {
        return ActionStatus.of(
            FlywheelsCode.MOVING_TO_RPM,
            StatusCodes.TARGET_STATUS + Math.round(targetRPM) + " RPM");
      }
    }
  }
}
