// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.requests.moduleRequests;

import com.stzteam.features.dictionary.Dictionary.StatusCodes;
import com.stzteam.features.marsprocessor.CreateCommand;
import com.stzteam.features.marsprocessor.RequestFactory;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.requests.Request;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.configuration.constants.ModuleConstants.TurretConstants;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO.TurretInputs;
import frc.robot.diagnostics.TurretCode;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

@RequestFactory
public interface TurretRequest extends Request<TurretInputs, TurretIO> {

  @CreateCommand(name = "stop")
  public static class Idle implements TurretRequest {
    @Override
    public ActionStatus apply(TurretInputs data, TurretIO actor) {
      actor.stop();
      data.targetAngle = data.angle;
      return ActionStatus.of(TurretCode.IDLE, "Idle");
    }
  }

  @CreateCommand(name = "manualControl")
  public static class manualControl implements TurretRequest {
    private DoubleSupplier stick;

    public manualControl joystick(DoubleSupplier stick) {
      this.stick = stick;
      return this;
    }

    @Override
    public ActionStatus apply(TurretInputs data, TurretIO actor) {
      if (data.angle.getDegrees() < 90 && data.angle.getDegrees() > -90) {
        actor.setSpeed(stick.getAsDouble() * 0.5);
      } else {
        actor.setSpeed(0);
      }
      return ActionStatus.of(TurretCode.MANUAL_CONTROL, "Manual");
    }
  }

  @CreateCommand(name = "voltageCommand")
  public static class SysIdOpenLoop implements TurretRequest {
    private double m_volts = 0;

    public SysIdOpenLoop withVolts(double volts) {
      this.m_volts = volts;
      return this;
    }

    @Override
    public ActionStatus apply(TurretInputs data, TurretIO actor) {
      actor.setVoltage(m_volts);
      data.targetAngle = data.angle;
      return ActionStatus.of(TurretCode.MANUAL_OVERRIDE, StatusCodes.MANUAL_STATUS + m_volts + "V");
    }
  }

  @CreateCommand(name = "toPosition")
  public static class Position implements TurretRequest {
    private Rotation2d m_targetAngle = new Rotation2d();
    private double toleranceDegrees = 1.0;

    public Position withTargetAngle(Rotation2d angle) {
      this.m_targetAngle = angle;
      return this;
    }

    public Position withTolerance(double tolerance) {
      this.toleranceDegrees = tolerance;
      return this;
    }

    @Override
    public ActionStatus apply(TurretInputs data, TurretIO actor) {
      data.targetAngle = m_targetAngle;
      actor.setPosition(m_targetAngle);

      boolean isLocked =
          MathUtil.isNear(m_targetAngle.getDegrees(), data.angle.getDegrees(), toleranceDegrees);

      if (isLocked) {
        return ActionStatus.of(TurretCode.LOCKED, StatusCodes.TARGETREACHED_STATUS);
      } else {
        return ActionStatus.of(
            TurretCode.TRACKING,
            StatusCodes.TARGET_STATUS + Math.round(m_targetAngle.getDegrees()) + "°");
      }
    }
  }

  @CreateCommand(name = "lockToTarget")
  public static class LockOnTarget implements TurretRequest {

    private Supplier<Translation2d> targetSupplier;
    private DoubleSupplier rotationSupplier;
    private double toleranceDegrees = 1.5;

    public LockOnTarget withTarget(Supplier<Translation2d> targetSupplier) {
      this.targetSupplier = targetSupplier;
      return this;
    }

    public LockOnTarget withChassisOmega(DoubleSupplier rotationSupplier) {
      this.rotationSupplier = rotationSupplier;
      return this;
    }

    public LockOnTarget withTolerance(double degrees) {
      this.toleranceDegrees = degrees;
      return this;
    }

    @Override
    public ActionStatus apply(TurretInputs data, TurretIO actor) {

      Translation2d target = targetSupplier.get();

      double chassisOmega = rotationSupplier.getAsDouble();
      /*
      Optional<Alliance> alliance = DriverStation.getAlliance();
      if (alliance.isPresent()) {
        if (alliance.get() == Alliance.Red) {
          target = AllianceUtil.flip(targetSupplier.get());
        }
      }*/

      Translation2d currentTarget = target;

      Pose2d transformedRobotPose =
          data.robotPose.transformBy(TurretConstants.ROBOT_TO_TURRET_TRANSFORM);

      Translation2d turretPose = transformedRobotPose.getTranslation();

      Translation2d robotToTarget = currentTarget.minus(turretPose);

      Rotation2d fieldAngle = robotToTarget.getAngle();

      Rotation2d turretSetpoint = fieldAngle.minus(data.robotPose.getRotation());

      double cleanDegrees = -MathUtil.inputModulus(turretSetpoint.getDegrees(), -180, 180);
      Rotation2d targetRot = Rotation2d.fromDegrees(cleanDegrees);

      data.targetAngle = targetRot;

      double ffVolts = chassisOmega * TurretConstants.kChassisAngularCompensator;

      actor.setPositionWithFF(targetRot, ffVolts);

      boolean isLocked =
          MathUtil.isNear(targetRot.getDegrees(), data.angle.getDegrees(), toleranceDegrees);

      if (isLocked) {
        return ActionStatus.of(TurretCode.LOCKED, StatusCodes.LOCK_STATUS);
      } else {
        return ActionStatus.of(TurretCode.TRACKING, StatusCodes.MOVING_STATUS);
      }
    }
  }
}
