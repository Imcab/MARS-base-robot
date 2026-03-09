// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.stzteam.features.limelight.LimelightConfig;
import com.stzteam.features.limelight.LimelightDriver;
import com.stzteam.features.limelight.LimelightNode;
import com.stzteam.features.limelight.LimelightNode.LimelightMsg;
import com.stzteam.mars.models.containers.IRobotContainer;
import com.stzteam.mars.operator.ControllerOI;
import com.stzteam.mars.services.nodes.FallbackNode;
import com.stzteam.mars.services.nodes.Node;
import com.stzteam.mars.test.TestRoutine;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.configuration.KeyManager;
import frc.robot.configuration.Manifest;
import frc.robot.configuration.Manifest.ArmBuilder;
import frc.robot.configuration.Manifest.ControlsBuilder;
import frc.robot.configuration.Manifest.DrivetrainBuilder;
import frc.robot.configuration.Manifest.FlywheelIntakeBuilder;
import frc.robot.configuration.Manifest.FlywheelShooterBuilder;
import frc.robot.configuration.Manifest.IndexerBuilder;
import frc.robot.configuration.Manifest.IntakeBuilder;
import frc.robot.configuration.Manifest.TrajectoryBuilder;
import frc.robot.configuration.Manifest.TurretBuilder;
import frc.robot.configuration.Manifest.VisualizerBuilder;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode.GamePieceMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode.TrajectoryMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.visualizer.VisualizerNode.VisualizerMsg;
import frc.robot.configuration.bindings.DriverBindings;
import frc.robot.configuration.bindings.OperatorBindings;
import frc.robot.configuration.bindings.TestBindings;
import frc.robot.configuration.constants.Constants;
import frc.robot.configuration.constants.ModuleConstants.VisionConstants;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.requests.moduleRequests.ArmRequestFactory;
import frc.robot.core.requests.moduleRequests.FlyWheelRequestFactory;
import frc.robot.core.requests.moduleRequests.IntakeRequestFactory;

public class RobotContainer implements IRobotContainer {

  public final ControllerOI driver;
  public final ControllerOI operator;

  public final CommandSwerveDrivetrain drivetrain;

  public SendableChooser<Command> chooser = new SendableChooser<>();
  public PathPlannerAuto eatAuto;
  public PathPlannerAuto AutoCenter;
  public PathPlannerAuto autoForeward;
  public PathPlannerAuto elipse;
  public PathPlannerAuto bumpPost;

  public final Arm arm;
  public final Turret turret;
  public final FlyWheel flywheelShooter;
  public final FlyWheel flywheelIntake;
  public final Intake intake;
  public final Indexer index;

  private final Node<LimelightMsg> limelightNode;

  private final Node<VisualizerMsg> virtualRobot;
  private final Node<TrajectoryMsg> trajetorySim;
  private final Node<GamePieceMsg> gamePieceViz;

  public final Superstructure superstructure;

  public final TestBindings tests;

  public void configureAutos() {
    NamedCommands.registerCommand(
        "Angle->Eat", superstructure.EatAutoAngle(-140, 4, intakeMODE.kDOWN, -10).withTimeout(4.5));
    NamedCommands.registerCommand("Eat", superstructure.EatAutoWheels(-10));
    NamedCommands.registerCommand(
        "IntakeUp",
        superstructure
            .getIntake()
            .setControl(() -> IntakeRequestFactory.setAngle().withAngle(-10).Tolerance(2)));
    NamedCommands.registerCommand(
        "Shoot",
        superstructure.shootOnTheMove(
            superstructure.getVirtualTarget(),
            ArmRequestFactory.interpolateTarget()
                .withDistance(() -> superstructure.getVirtualDistance())
                .withTolerance(Constants.ARM_TOLERANCE),
            FlyWheelRequestFactory.interpolateRPM()
                .withDistance(() -> superstructure.getVirtualDistance())
                .withTolerance(Constants.FLYWHEEL_TOLERANCE),
            12));
    NamedCommands.registerCommand("Shoot", superstructure.shootAuto().withTimeout(10));

    eatAuto = new PathPlannerAuto("EatAuto1");
    AutoCenter = new PathPlannerAuto("AutoCenter");
    autoForeward = new PathPlannerAuto("New Auto");
    elipse = new PathPlannerAuto("elipse");
    bumpPost = new PathPlannerAuto("EatPost-auto");

    chooser.setDefaultOption("EatAuto", eatAuto);
    chooser.addOption("AutoCenter", AutoCenter);
    chooser.addOption("test1Forward", autoForeward);
    chooser.addOption("Elipse", elipse);
    chooser.addOption("Post", bumpPost);

    SmartDashboard.putData("AutoSelector", chooser);
  }

  public RobotContainer() {

    // WHEELS AHHH
    // GG PAPA
    // GGGGGGGG
    // Banana Chong 2
    // Banana Chong
    // Branch Chong gg papa

    this.driver = ControlsBuilder.buildDriver();

    this.operator = ControlsBuilder.buildOperator();

    this.drivetrain = DrivetrainBuilder.buildModule();

    this.turret = TurretBuilder.create().withDrivetrain(drivetrain).buildModule();
    this.arm = ArmBuilder.create().buildModule();
    this.intake = IntakeBuilder.create().buildModule();
    this.index = IndexerBuilder.create().buildModule();
    this.flywheelShooter = FlywheelShooterBuilder.create().buildModule();
    this.flywheelIntake = FlywheelIntakeBuilder.create().buildModule();

    this.superstructure =
        Manifest.SuperstructureBuilder.superBuild(
            this.turret,
            this.arm,
            this.intake,
            this.index,
            this.flywheelShooter,
            this.flywheelIntake);

    LimelightConfig config =
        new LimelightConfig()
            .withMaxValidDistanceMeters(VisionConstants.MAX_VALID_DISTANCE_METERS)
            .withMaxAngularVelocity(VisionConstants.MAX_ANGULAR_VELOCITY_DEG_PER_SEC)
            .withMultiTagStdDev(VisionConstants.MULTI_TAG_STD_DEV)
            .withRotationStdDev(VisionConstants.ROTATION_STD_DEV)
            .withDefaultStdDevs(VisionConstants.DEFAULT_STD_DEVS)
            .withSingleTagBaseStdDev(VisionConstants.SINGLE_TAG_BASE_STD_DEV)
            .withSingleTagDistanceMultiplier(VisionConstants.SINGLE_TAG_DISTANCE_MULTIPLIER);

    this.limelightNode =
        !Manifest.HAS_LIMELIGHT
            ? new FallbackNode<>()
            : new LimelightNode(
                config,
                new LimelightDriver(
                    () -> drivetrain.getPigeon2().getRotation2d(),
                    () -> drivetrain.getPigeon2().getAngularVelocityZWorld().getValueAsDouble()),
                msg -> {
                  if (msg.validPose) {
                    drivetrain.addVisionMeasurement(msg.botPose, msg.timestamp, msg.stdDevs);
                  }
                });

    this.virtualRobot =
        VisualizerBuilder.buildNode(
            turret::getDegrees,
            () -> arm.getState().position,
            () -> intake.getState().position,
            msg -> msg.telemeterize(KeyManager.VISUALIZER_KEY + KeyManager.COMPONENTS_KEY));

    this.trajetorySim =
        TrajectoryBuilder.buildNode(
            () -> drivetrain.getState().Pose,
            turret::getDegrees,
            () -> arm.getState().position,
            msg -> msg.telemeterize(KeyManager.VISUALIZER_KEY + KeyManager.TRAJECTORY_KEY));

    this.gamePieceViz =
        Manifest.GamePieceBuilder.buildNode(
            msg -> msg.telemeterize(KeyManager.VISUALIZER_KEY + KeyManager.GAMEPIECE_KEY));

    DriverBindings.create(drivetrain, driver).bind();

    OperatorBindings.create(operator, superstructure).withNodes(gamePieceViz, trajetorySim).bind();

    tests = TestBindings.create(superstructure);

    tests.bind();

    configureAutos();
  }

  @Override
  public void updateNodes() {

    limelightNode.periodic();

    // virtualRobot.periodic();
    // trajetorySim.periodic();
    // gamePieceViz.periodic();

  }

  @Override
  public Command getAutonomousCommand() {
    return chooser.getSelected();
  }

  @Override
  public TestRoutine getTestRoutine() {
    return tests.getSelected();
  }
}
