// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.modules.turretmodule;

import com.stzteam.features.dictionary.Dictionary.CommonTables;
import com.stzteam.features.dictionary.Dictionary.CommonTables.Terminology;
import com.stzteam.features.unitprocessor.Unit;
import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.models.Telemetry;
import com.stzteam.mars.models.singlemodule.ModularSubsystem;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.configuration.KeyManager;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO.TurretInputs;
import frc.robot.core.requests.moduleRequests.TurretCommands;
import frc.robot.core.requests.moduleRequests.TurretRequest;
import java.util.function.Supplier;

public class Turret extends ModularSubsystem<TurretInputs, TurretIO> implements TurretCommands {

  private final Supplier<Pose2d> poseSupplier;
  private final Supplier<ChassisSpeeds> speedSupplier;

  public Turret(TurretIO io, Supplier<Pose2d> pose, Supplier<ChassisSpeeds> speeds) {
    super(
        SubsystemBuilder.<TurretInputs, TurretIO>setup()
            .key(KeyManager.TURRET_KEY)
            .hardware(io, new TurretInputs())
            .request(new TurretRequest.Idle())
            .telemetry(new TurretTelemetry()));

    this.poseSupplier = pose;
    this.speedSupplier = speeds;

    setDefaultCommand(runRequest(() -> new TurretRequest.Idle()));
  }

  public boolean isAtTarget(double toleranceDegrees) {
    return MathUtil.isNear(
        inputs.targetAngle.getDegrees(), inputs.angle.getDegrees(), toleranceDegrees);
  }

  @Override
  public TurretInputs getState() {
    return inputs;
  }

  @Override
  public Command setControl(Supplier<TurretRequest> request) {
    return runRequest(request);
  }

  @Unit(value = "Meters", group = "Turret")
  public double distanceTo(Translation2d point) {
    return poseSupplier.get().getTranslation().getDistance(point);
  }

  public Pose2d getRobotPose() {
    return poseSupplier.get();
  }

  public ChassisSpeeds getRobotSpeeds() {
    return speedSupplier.get();
  }

  @Override
  public void absolutePeriodic(TurretInputs data) {
    data.robotPose = poseSupplier.get();
    data.robotSpeed = speedSupplier.get();
  }

  public double getDegrees() {
    return inputs.angle.getDegrees();
  }

  public Rotation2d getRotation() {
    return inputs.angle;
  }

  public static class TurretTelemetry extends Telemetry<TurretInputs> {

    @Override
    public void telemeterize(TurretInputs data, ActionStatus lastStatus) {

      String table = KeyManager.TURRET_KEY;

      NetworkIO.set(table, CommonTables.APPLIED_KEY + CommonTables.OUTPUT_KEY, data.appliedVolts);
      NetworkIO.set(
          table, CommonTables.ANGLE_KEY + CommonTables.DEGREES_KEY, data.angle.getDegrees());
      NetworkIO.set(
          table,
          CommonTables.TARGET_KEY + CommonTables.ANGLE_KEY + Terminology.DEG,
          data.targetAngle.getDegrees());
      NetworkIO.set(table, CommonTables.VELOCITY_KEY + Terminology.RPS, data.velocityRPS);
      NetworkIO.set(
          table,
          CommonTables.LATENCY_KEY + Terminology.MS,
          (Timer.getFPGATimestamp() - data.timestamp) * 1000);

      NetworkIO.set(table, CommonTables.PAYLOAD_NAME_KEY, lastStatus.getPayload().name());
      NetworkIO.set(table, CommonTables.PAYLOAD_MESSAGE_KEY, lastStatus.getPayload().message());
      NetworkIO.set(table, CommonTables.PAYLOAD_HEX_KEY, lastStatus.getPayload().colorHex());
    }
<<<<<<< HEAD
  }
}
=======

    public Command setControl(Supplier<TurretRequest> request){
        return runRequest(request);
    }

    @Unit(value = "Meters", group = "Turret")
    public double distanceTo(Translation2d point){
        return poseSupplier.get().getTranslation().getDistance(point);
    }

    public Pose2d getRobotPose() {
        return poseSupplier.get();
    }

    public ChassisSpeeds getRobotSpeeds() {
        return speedSupplier.get();
    }

    @Override
    public void absolutePeriodic(TurretInputs data) {
        data.robotPose = poseSupplier.get();
        data.robotSpeed = speedSupplier.get();
    }

    public double getDegrees(){
        return inputs.angle.getDegrees();
    }

    public Rotation2d getRotation(){
        return inputs.angle;
    }

    public static class TurretTelemetry extends Telemetry<TurretInputs>{

        @Override
        public void telemeterize(TurretInputs data, ActionStatus lastStatus) {

            String table = KeyManager.TURRET_KEY;

            NetworkIO.set(table, CommonTables.APPLIED_KEY + CommonTables.OUTPUT_KEY, data.appliedVolts);
            NetworkIO.set(table, CommonTables.ANGLE_KEY + CommonTables.DEGREES_KEY, data.angle.getDegrees());
            NetworkIO.set(table, CommonTables.TARGET_KEY + CommonTables.ANGLE_KEY + Terminology.DEG, data.targetAngle.getDegrees());
            NetworkIO.set(table, CommonTables.VELOCITY_KEY + Terminology.RPS, data.velocityRPS);
            NetworkIO.set(table, CommonTables.LATENCY_KEY + Terminology.MS, (Timer.getFPGATimestamp() - data.timestamp) * 1000);

            NetworkIO.set(table, CommonTables.PAYLOAD_NAME_KEY, KeyManager.TURRET_KEY);
            NetworkIO.set(table, CommonTables.PAYLOAD_MESSAGE_KEY, lastStatus.getPayload().message());
            NetworkIO.set(table, CommonTables.PAYLOAD_HEX_KEY, lastStatus.getPayload().colorHex());
            

        }
        
    }
}
>>>>>>> 2fec589affc2ebc8e258210e99e5c004ba0c2607
