// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration;

import com.stzteam.mars.builder.Environment;
import com.stzteam.mars.builder.Environment.RunMode;
import com.stzteam.mars.builder.Injector;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.operator.ControllerOI;
import com.stzteam.mars.operator.PS5OI;
import com.stzteam.mars.operator.XboxOI;
import com.stzteam.mars.services.nodes.Node;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode.GamePieceMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode.TrajectoryMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.visualizer.VisualizerNode;
import frc.robot.configuration.advantageScope.visuals.nodes.visualizer.VisualizerNode.VisualizerMsg;
import frc.robot.configuration.constants.ModuleConstants.TunerConstants;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.modules.superstructure.composite.SuperstructureData;
import frc.robot.core.modules.superstructure.composite.SuperstructureIO;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIO;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOFallback;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOKraken;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOSim;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel.idleMode;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIOFallback;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIOKrakenIntake;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIOKrakenShooter;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIOSim;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerIO;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerIOFallback;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerIOSim;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerSparkMax;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIO;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOFallback;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOSim;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIOFallback;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIOSim;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIOSparkMax;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.modules.swerve.SwerveTelemetry;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class Manifest {

  public enum ControllerType {
    PS5,
    XBOX
  }

  private static final int DRIVER_PORT = 0;
  private static final int OPERATOR_PORT = 1;

  public static final RunMode CURRENT_MODE = RunMode.SIM;

  static {
    Environment.setMode(CURRENT_MODE);
  }

  public static final ControllerType DRIVER_CONTROLLER = ControllerType.XBOX;
  public static final ControllerType OPERATOR_CONTROLLER = ControllerType.XBOX;

  public static final boolean HAS_MARS_GCS = true;

  public static final boolean HAS_DRIVETRAIN = true;

  public static final boolean HAS_VISUALS = true;
  public static final boolean HAS_TRAJ_VISUAL = true;
  public static final boolean HAS_FUEL_VISUAL = true;
  public static final boolean HAS_TURRET = true;
  public static final boolean HAS_ARM = true;
  public static final boolean HAS_LIMELIGHT = false; // NO CAMBIAR
  public static final boolean HAS_INDEXER = true;
  public static final boolean HAS_QUESTNAV = false;
  public static final boolean HAS_SHOOTER_WHEELS = true;
  public static final boolean HAS_INTAKE = true;
  public static final boolean HAS_INTAKE_WHEELS = true;

  public static class SuperstructureBuilder {
    public static Superstructure superBuild(
        Turret turret,
        Arm arm,
        Intake intake,
        Indexer indexer,
        FlyWheel flywheelShooter,
        FlyWheel flywheelIntake) {

      SuperstructureIO io =
          new SuperstructureIO(turret, arm, intake, indexer, flywheelShooter, flywheelIntake);

      return new Superstructure(
          SubsystemBuilder.<SuperstructureData, SuperstructureIO>setup()
              .key(KeyManager.SUPERSTRUCTURE_KEY)
              .hardware(io, new SuperstructureData()));
    }
  }

  public static Node<VisualizerMsg> buildVisualizerNode(
      DoubleSupplier turretAngleSupplier,
      DoubleSupplier hoodAngleSupplier,
      DoubleSupplier intakeAngleSupplier,
      Consumer<VisualizerMsg> topicPublisher) {

    return Injector.createNode(
        HAS_VISUALS,
        () ->
            new VisualizerNode(
                KeyManager.VISUALIZER_KEY + KeyManager.COMPONENTS_KEY,
                turretAngleSupplier,
                hoodAngleSupplier,
                intakeAngleSupplier,
                topicPublisher));
  }

  public static Node<TrajectoryMsg> buildTrajectoryNode(
      CommandSwerveDrivetrain dt, Turret turret, Arm arm, Consumer<TrajectoryMsg> publisher) {

    return Injector.createNode(
        HAS_TRAJ_VISUAL,
        () ->
            new TrajectoryNode(
                KeyManager.VISUALIZER_KEY + KeyManager.TRAJECTORY_KEY,
                () -> dt.getState().Pose,
                turret::getDegrees,
                () -> arm.getState().position,
                publisher));
  }

  public static Node<GamePieceMsg> buildGamePieceNode(Consumer<GamePieceMsg> publisher) {
    return Injector.createNode(
        HAS_FUEL_VISUAL,
        () -> new GamePieceNode(KeyManager.VISUALIZER_KEY + KeyManager.GAMEPIECE_KEY, publisher));
  }

  public static class ControlsBuilder {

    public static ControllerOI buildDriver() {
      return DRIVER_CONTROLLER == ControllerType.PS5
          ? new PS5OI(DRIVER_PORT)
          : new XboxOI(DRIVER_PORT);
    }

    public static ControllerOI buildOperator() {
      return OPERATOR_CONTROLLER == ControllerType.PS5
          ? new PS5OI(OPERATOR_PORT)
          : new XboxOI(OPERATOR_PORT);
    }
  }

  public static class DrivetrainBuilder {

    public static CommandSwerveDrivetrain buildModule() {
      if (!HAS_DRIVETRAIN) return null;

      CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

      SwerveTelemetry telemetry = new SwerveTelemetry();
      drivetrain.registerTelemetry(telemetry::telemeterize);

      return drivetrain;
    }
  }

  public static FlyWheel buildFlywheelShooter() {
    FlyWheelIO io =
        Injector.createIO(
            HAS_SHOOTER_WHEELS,
            FlyWheelIOFallback::new,
            FlyWheelIOKrakenShooter::new,
            FlyWheelIOSim::new);

    return new FlyWheel(io, KeyManager.FLYWHEEL_OUTAKE_KEY, idleMode.outakeIDLE);
  }

  public static FlyWheel buildFlywheelIntake() {
    FlyWheelIO io =
        Injector.createIO(
            HAS_INTAKE_WHEELS,
            FlyWheelIOFallback::new,
            FlyWheelIOKrakenIntake::new,
            FlyWheelIOSim::new);

    return new FlyWheel(io, KeyManager.FLYWHEEL_INTAKE_KEY, idleMode.intakeIDLE);
  }

  public static Arm buildArm() {
    ArmIO io = Injector.createIO(HAS_ARM, ArmIOFallback::new, ArmIOKraken::new, ArmIOSim::new);
    return new Arm(io);
  }

  public static Intake buildIntake() {
    IntakeIO io =
        Injector.createIO(HAS_INTAKE, IntakeIOFallback::new, IntakeIOKraken::new, IntakeIOSim::new);
    return new Intake(io);
  }

  public static Indexer buildIndexer() {
    IndexerIO io =
        Injector.createIO(
            HAS_INDEXER, IndexerIOFallback::new, IndexerSparkMax::new, IndexerIOSim::new);
    return new Indexer(io);
  }

  public static Turret buildTurret(CommandSwerveDrivetrain dt) {
    if (HAS_TURRET && dt == null) {
      throw new IllegalStateException("Falta el Drivetrain en la Torreta.");
    }

    TurretIO io =
        Injector.createIO(
            HAS_TURRET, TurretIOFallback::new, TurretIOSparkMax::new, TurretIOSim::new);

    Supplier<Pose2d> pose = (dt != null) ? () -> dt.getState().Pose : () -> Pose2d.kZero;
    Supplier<ChassisSpeeds> speeds = (dt != null) ? dt::getChassisSpeeds : ChassisSpeeds::new;

    return new Turret(io, pose, speeds);
  }
}
