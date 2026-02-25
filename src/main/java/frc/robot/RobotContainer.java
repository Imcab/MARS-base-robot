// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.CANBus;
import com.stzteam.forgemini.io.SmartChooser;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.KeyManager;
import frc.robot.configuration.Manifest;
import frc.robot.configuration.Manifest.ArmBuilder;
import frc.robot.configuration.Manifest.AutoBuilder;
import frc.robot.configuration.Manifest.ControlsBuilder;
import frc.robot.configuration.Manifest.DrivetrainBuilder;
import frc.robot.configuration.Manifest.FlywheelIntakeBuilder;
import frc.robot.configuration.Manifest.FlywheelShooterBuilder;
import frc.robot.configuration.Manifest.IndexerBuilder;
import frc.robot.configuration.Manifest.IntakeBuilder;
import frc.robot.configuration.Manifest.TrajectoryBuilder;
import frc.robot.configuration.Manifest.TurretBuilder;
import frc.robot.configuration.Manifest.VisionBuilder;
import frc.robot.configuration.Manifest.VisualizerBuilder;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode.GamePieceMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode.TrajectoryMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.visualizer.VisualizerNode.VisualizerMsg;
import frc.robot.configuration.bindings.AutoBindings;
import frc.robot.configuration.bindings.DriverBindings;
import frc.robot.configuration.bindings.OperatorBindings;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.modules.swerve.visionNode.VisionNode.VisionMsg;
import mars.source.builder.Environment;
import mars.source.builder.RunMode;
import mars.source.models.containers.IRobotContainer;
import mars.source.operator.ControllerOI;
import mars.source.services.nodes.Node;

public class RobotContainer implements IRobotContainer{

  public final ControllerOI driver;
  public final ControllerOI operator;

  public final SmartChooser<Command> autoChooser;

  public final CommandSwerveDrivetrain drivetrain;
  
  public final Arm arm;
  public final Turret turret;
  public final FlyWheel flywheelShooter;
  public final FlyWheel flywheelIntake;
  public final Intake intake;
  public final Indexer index;

  public final Node<VisionMsg> limelight;
  public final Node<VisionMsg> questnav;

  private final Node<VisualizerMsg> virtualRobot;
  private final Node<TrajectoryMsg> trajetorySim;
  private final Node<GamePieceMsg> gamePieceViz;

  public final Superstructure superstructure;

  public RobotContainer() {

    //WHEELS AHHH
    //GG PAPA
    //GGGGGGGG
    //Banana Chong 2
    //Banana Chong

    this.driver = ControlsBuilder.buildDriver();

    this.operator = ControlsBuilder.buildOperator();

    this.drivetrain = DrivetrainBuilder.buildModule();

    this.limelight = VisionBuilder.limelightNode(
            () -> drivetrain.getPigeon2().getRotation2d(),
            () -> drivetrain.getPigeon2().getAngularVelocityZWorld().getValueAsDouble(), 
            drivetrain::consumeVisionData
    );

    this.questnav = VisionBuilder.questNode(drivetrain::consumeVisionData);

    this.turret = TurretBuilder.create().withDrivetrain(drivetrain).buildModule();
    this.arm = ArmBuilder.create().buildModule();
    this.intake = IntakeBuilder.create().buildModule();
    this.index = IndexerBuilder.create().buildModule();
    this.flywheelShooter = FlywheelShooterBuilder.create().buildModule();
    this.flywheelIntake = FlywheelIntakeBuilder.create().buildModule();

    this.superstructure = Manifest.SuperstructureBuilder.superBuild(
        this.turret, this.arm, this.intake, this.index, this.flywheelShooter, this.flywheelIntake
    );

    this.autoChooser = AutoBuilder.build(KeyManager.AUTOCHOOSER_KEY);

    this.virtualRobot = VisualizerBuilder.buildNode(
      turret::getDegrees,
      () -> arm.getState().position,
      () -> intake.getState().position,
      msg -> msg.telemeterize(KeyManager.VISUALIZER_KEY + KeyManager.COMPONENTS_KEY)
    );

    this.trajetorySim = TrajectoryBuilder.buildNode(
      () -> drivetrain.getState().Pose,
      turret::getDegrees,
      () -> arm.getState().position,
      msg -> msg.telemeterize(KeyManager.VISUALIZER_KEY + KeyManager.TRAJECTORY_KEY)
    );

    this.gamePieceViz = Manifest.GamePieceBuilder.buildNode(
        msg -> msg.telemeterize(KeyManager.VISUALIZER_KEY + KeyManager.GAMEPIECE_KEY)
    );

    AutoBindings.create(autoChooser)
    .withDrivetrain(drivetrain)
    .withNodes(questnav)
    .bind();
    
    DriverBindings.parameterized(drivetrain, driver).bind();

    OperatorBindings.create(operator, superstructure)
    .withSubsystems(turret, arm, intake, drivetrain).
    withNodes(gamePieceViz, trajetorySim)
    .bind();

  }

  @Override
  public void updateNodes() {

      if(Environment.getMode() == RunMode.REAL){
        limelight.periodic();
        questnav.periodic();
      }
      
      virtualRobot.periodic();
      trajetorySim.periodic();
      gamePieceViz.periodic();
      
  }

  @Override
  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
