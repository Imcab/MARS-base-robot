// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.composite;

import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.forgemini.io.Signal;
import com.stzteam.forgemini.io.Tunable;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.models.multimodules.CompositeSubsystem;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.KeyManager;
import frc.robot.configuration.constants.Constants;
import frc.robot.configuration.constants.ModuleConstants.TurretConstants;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOKraken.ArmMODE;
import frc.robot.core.modules.superstructure.modules.climbermodule.Climber;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.requests.moduleRequests.ArmRequest;
import frc.robot.core.requests.moduleRequests.ArmRequestFactory;
import frc.robot.core.requests.moduleRequests.FlyWheelRequest;
import frc.robot.core.requests.moduleRequests.FlyWheelRequestFactory;
import frc.robot.core.requests.moduleRequests.IndexerRequestFactory;
import frc.robot.core.requests.moduleRequests.TurretRequestFactory;
import java.util.function.DoubleSupplier;

public class Superstructure extends CompositeSubsystem<SuperstructureData, SuperstructureIO> {

  private static final double NOMINAL_FUEL_VELOCITY_MPS = 18.0;
  private static final double intakeVolts = -5;

  @Tunable public double RPMTest = -3500;

  @Tunable public double AngleTest = -20;

  public Superstructure(SubsystemBuilder<SuperstructureData, SuperstructureIO> builder) {
    super(builder);
  }

  public Climber getClimber() {
    return getSubsystem(KeyManager.CLIMBER_KEY);
  }

  public Turret getTurret() {
    return getSubsystem(KeyManager.TURRET_KEY);
  }

  public Arm getArm() {
    return getSubsystem(KeyManager.ARM_KEY);
  }

  public Intake getIntake() {
    return getSubsystem(KeyManager.INTAKE_KEY);
  }

  public Indexer getIndexer() {
    return getSubsystem(KeyManager.INDEX_KEY);
  }

  public FlyWheel getFlyWheelsIntake() {
    return getSubsystem(KeyManager.FLYWHEEL_INTAKE_KEY);
  }

  public FlyWheel getFlywheelShooter() {
    return getSubsystem(KeyManager.FLYWHEEL_OUTAKE_KEY);
  }

  public Translation2d getVirtualTarget() {
    Turret turret = getTurret();

    Pose2d robotPose = turret.getRobotPose();

    ChassisSpeeds speeds = turret.getRobotSpeeds();

    // Proyectamos dónde estará el chasis en 30ms
    double phaseDelay = 0.03;
    Twist2d futureTwist =
        new Twist2d(
            speeds.vxMetersPerSecond * phaseDelay,
            speeds.vyMetersPerSecond * phaseDelay,
            speeds.omegaRadiansPerSecond * phaseDelay);

    // empuja la pose actual hacia el futuro usando twist
    Pose2d futureRobotPose = robotPose.exp(futureTwist);

    // Transformamos la pose futura del robot a la pose futra de la torreta
    Pose2d turretPose = futureRobotPose.transformBy(TurretConstants.ROBOT_TO_TURRET_TRANSFORM);

    // Vector de velocidad del chasis proyectado al campo
    Translation2d robotVelVector =
        new Translation2d(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond)
            .rotateBy(futureRobotPose.getRotation());

    Translation2d realTarget = Constants.HUB_LOCATION.toTranslation2d();

    // Distancia inicial usando la posición del futuro cercano
    double distance = turretPose.getTranslation().getDistance(realTarget);
    double timeOfFlight = distance / NOMINAL_FUEL_VELOCITY_MPS;

    Translation2d virtualTarget = realTarget;

    for (int i = 0; i < 20; i++) {
      virtualTarget = realTarget.minus(robotVelVector.times(timeOfFlight));
      double newDistance = turretPose.getTranslation().getDistance(virtualTarget);
      timeOfFlight = newDistance / NOMINAL_FUEL_VELOCITY_MPS;
    }

    return virtualTarget;
  }

  public double getVirtualDistance() {
    Turret turret = getSubsystem(KeyManager.TURRET_KEY);

    Pose2d turretPose =
        turret.getRobotPose().transformBy(TurretConstants.ROBOT_TO_TURRET_TRANSFORM);

    return turretPose.getTranslation().getDistance(getVirtualTarget());
  }

  public Command lockToHub() {
    Turret turret = getTurret();

    return Commands.parallel(
        turret.setControl(
            () ->
                TurretRequestFactory.lockOnTarget()
                    .withTarget(() -> this.getVirtualTarget())
                    .withChassisOmega(() -> turret.getRobotSpeeds().omegaRadiansPerSecond)));
  }

  public Command shoot() {
    FlyWheel flywheelShooter = getFlywheelShooter();
    Indexer index = getIndexer();

    return Commands.sequence(
        Commands.parallel(
                flywheelShooter.runRequest(
                    () ->
                        FlyWheelRequestFactory.setRPM()
                            .toRPM(-3000)
                            .withTolerance(Constants.FLYWHEEL_TOLERANCE)))
            .until(() -> flywheelShooter.isAtTarget(Constants.FLYWHEEL_TOLERANCE)),
        index.setControl(() -> IndexerRequestFactory.moveVoltage().withIndex(8)));
  }

  public Command eatCommand() {
    FlyWheel intakeWheels = getFlyWheelsIntake();
    Indexer index = getIndexer();

    return Commands.parallel(
        intakeWheels.spinAtVoltage(intakeVolts),
        index.setControl(() -> IndexerRequestFactory.moveVoltage().withIndex(0).withRollers(8)));
  }

  public Command clearFuel() {
    FlyWheel intakeFlyWheel = getFlyWheelsIntake();
    Indexer index = getIndexer();

    return Commands.parallel(
        intakeFlyWheel.spinAtVoltage(-intakeVolts),
        Commands.sequence(
                index
                    .setControl(
                        () -> IndexerRequestFactory.moveVoltage().withRollers(-12).withIndex(-12))
                    .withTimeout(1.5),
                index
                    .setControl(
                        () -> IndexerRequestFactory.moveVoltage().withRollers(12).withIndex(12))
                    .withTimeout(1.5))
            .repeatedly());
  }

  public Command ShootAngleTest(DoubleSupplier armAngle, DoubleSupplier rpm) {
    Turret turret = getTurret();
    FlyWheel flywheelShooter = getFlywheelShooter();
    Indexer index = getIndexer();
    Arm arm = getArm();
    FlyWheel intakeWheels = getFlyWheelsIntake();

    return Commands.sequence(
        Commands.parallel(
                turret.setControl(
                    () ->
                        TurretRequestFactory.lockOnTarget()
                            .withTarget(() -> this.getVirtualTarget())
                            .withTolerance(Constants.TURRET_TOLERANCE)
                            .withChassisOmega(() -> turret.getRobotSpeeds().omegaRadiansPerSecond)),
                arm.setControl(
                    () ->
                        ArmRequestFactory.setAngle()
                            .withAngle(armAngle.getAsDouble())
                            .withTolerance(Constants.ARM_TOLERANCE)
                            .withMode(ArmMODE.kUP)),
                flywheelShooter.runRequest(
                    () ->
                        FlyWheelRequestFactory.setRPM()
                            .toRPM(rpm.getAsDouble())
                            .withTolerance(Constants.FLYWHEEL_TOLERANCE)))
            .until(() -> flywheelShooter.isAtTarget(Constants.FLYWHEEL_TOLERANCE)),
        Commands.parallel(
            index.setControl(
                () -> IndexerRequestFactory.moveVoltage().withRollers(12).withIndex(12)),
            intakeWheels.setControl(
                () -> FlyWheelRequestFactory.moveVoltage().withVolts(intakeVolts))));
  }

  public Command shootOnTheMove(
      Translation2d turretTarget,
      ArmRequest armRequest,
      FlyWheelRequest shooterRequest,
      double voltIndex) {
    Turret turret = getSubsystem(KeyManager.TURRET_KEY);
    Arm arm = getSubsystem(KeyManager.ARM_KEY);
    FlyWheel flywheelShooter = getSubsystem(KeyManager.FLYWHEEL_OUTAKE_KEY);
    Indexer index = getSubsystem(KeyManager.INDEX_KEY);
    FlyWheel intakeWheels = getFlyWheelsIntake();

    return Commands.sequence(
        Commands.parallel(
                turret.setControl(
                    () ->
                        TurretRequestFactory.lockOnTarget()
                            .withTarget(() -> turretTarget)
                            .withTolerance(Constants.TURRET_TOLERANCE)
                            .withChassisOmega(() -> turret.getRobotSpeeds().omegaRadiansPerSecond)),
                arm.setControl(() -> armRequest),
                flywheelShooter.runRequest(() -> shooterRequest))
            .until(() -> flywheelShooter.isAtTarget(Constants.FLYWHEEL_TOLERANCE)),
        Commands.parallel(
            index.setControl(
                () ->
                    IndexerRequestFactory.moveVoltage()
                        .withRollers(voltIndex)
                        .withIndex(voltIndex)),
            intakeWheels.setControl(() -> FlyWheelRequestFactory.moveVoltage().withVolts(-10))));
  }

  public Command shootOnTheMoveDel(
    Translation2d turretTarget,
    ArmRequest armRequest,
    FlyWheelRequest shooterRequest,
    double voltIndex) {
  Turret turret = getSubsystem(KeyManager.TURRET_KEY);
  Arm arm = getSubsystem(KeyManager.ARM_KEY);
  FlyWheel flywheelShooter = getSubsystem(KeyManager.FLYWHEEL_OUTAKE_KEY);
  Indexer index = getSubsystem(KeyManager.INDEX_KEY);
  FlyWheel intakeWheels = getFlyWheelsIntake();

  return Commands.parallel(
      turret.setControl(
          () ->
              TurretRequestFactory.lockOnTarget()
                  .withTarget(() -> turretTarget)
                  .withTolerance(Constants.TURRET_TOLERANCE)
                  .withChassisOmega(() -> turret.getRobotSpeeds().omegaRadiansPerSecond)),
      arm.setControl(() -> armRequest),
      flywheelShooter.runRequest(() -> shooterRequest),
      

      index.setControl(
          () ->
              flywheelShooter.isAtTarget(Constants.FLYWHEEL_TOLERANCE)
                  ? IndexerRequestFactory.moveVoltage().withRollers(voltIndex).withIndex(voltIndex)
                  : IndexerRequestFactory.moveVoltage().withRollers(0).withIndex(0)),
                  
      intakeWheels.setControl(
          () -> 
              flywheelShooter.isAtTarget(Constants.FLYWHEEL_TOLERANCE)
                  ? FlyWheelRequestFactory.moveVoltage().withVolts(-10)
                  : FlyWheelRequestFactory.moveVoltage().withVolts(0)));
  }

  

  public Command shootAuto() {
    return Commands.sequence(
            this.shootOnTheMove(
                    this.getVirtualTarget(),
                    ArmRequestFactory.interpolateTarget()
                        .withDistance(() -> this.getVirtualDistance())
                        .withTolerance(Constants.ARM_TOLERANCE),
                    FlyWheelRequestFactory.interpolateRPM()
                        .withDistance(() -> this.getVirtualDistance())
                        .withTolerance(Constants.FLYWHEEL_TOLERANCE),
                    12)
                .withTimeout(3),
            this.shootOnTheMove(
                    this.getVirtualTarget(),
                    ArmRequestFactory.interpolateTarget()
                        .withDistance(() -> this.getVirtualDistance())
                        .withTolerance(Constants.ARM_TOLERANCE),
                    FlyWheelRequestFactory.interpolateRPM()
                        .withDistance(() -> this.getVirtualDistance())
                        .withTolerance(Constants.FLYWHEEL_TOLERANCE),
                    -12)
                .withTimeout(0.5))
        .repeatedly();
  }

  public Command EatAutoAngle(double angle, double tolerance, intakeMODE mode, double voltage) {

    Intake intake = getIntake();
    FlyWheel intakeWheels = getFlyWheelsIntake();

    return Commands.parallel(
        intake.toAngle(angle, mode, tolerance), intakeWheels.spinAtVoltage(voltage));
  }

  @Signal(key = "RPM", onChange = true)
  public double getRPM() {
    return RPMTest;
  }

  @Signal(key = "Angle", onChange = true)
  public double getAngle() {
    return AngleTest;
  }

  public Command EatAutoWheels(double voltage) {
    FlyWheel intakeWheels = getFlyWheelsIntake();

    return intakeWheels.spinAtVoltage(voltage);
  }

  public Command stopAll() {
    Turret turret = getTurret();
    Arm arm = getArm();
    FlyWheel flywheel = getFlyWheelsIntake();
    FlyWheel flywheelout = getFlywheelShooter();
    Indexer index = getIndexer();
    Intake intake = getIntake();

    return Commands.parallel(
        turret.stop(),
        arm.stop(),
        flywheel.runRequest(() -> FlyWheelRequestFactory.idleIntake()),
        flywheelout.runRequest(() -> FlyWheelRequestFactory.idleOutake()),
        index.stop(),
        intake.stop());
  }

  @Override
  public SuperstructureData getState() {
    return inputs;
  }

  @Override
  public void absolutePeriodic(SuperstructureData inputs) {
    NetworkIO.set(KeyManager.SUPERSTRUCTURE_KEY, "DistanceToHub", getVirtualDistance());
    NetworkIO.set(KeyManager.SUPERSTRUCTURE_KEY, "VirtualTarget", getVirtualTarget());
  }
}
