// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.stzteam.forgemini.io.SmartChooser;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.KeyManager;
import frc.robot.configuration.Manifest;
import frc.robot.configuration.Manifest.ArmBuilder;
import frc.robot.configuration.Manifest.AutoBuilder;
import frc.robot.configuration.Manifest.ControlsBuilder;
import frc.robot.configuration.Manifest.DrivetrainBuilder;
import frc.robot.configuration.Manifest.FlywheelBuilder;
import frc.robot.configuration.Manifest.IndexerBuilder;
import frc.robot.configuration.Manifest.IntakeBuilder;
import frc.robot.configuration.Manifest.TrajectoryBuilder;
import frc.robot.configuration.Manifest.TurretBuilder;
import frc.robot.configuration.Manifest.VisionBuilder;
import frc.robot.configuration.Manifest.VisualizerBuilder;
import frc.robot.configuration.advantageScope.visuals.nodes.GamePieceNode;
import frc.robot.configuration.advantageScope.visuals.nodes.TrajectoryNode;
import frc.robot.configuration.advantageScope.visuals.nodes.VisualizerNode;
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
import frc.robot.core.modules.swerve.nodes.LimelightNode;
import frc.robot.core.modules.swerve.nodes.QuestNavNode;
import mars.source.models.containers.IRobotContainer;
import mars.source.operator.ControllerOI;

public class RobotContainer implements IRobotContainer{

  public final ControllerOI driver;
  public final ControllerOI operator;

  public final SmartChooser<Command> autoChooser;

  public final CommandSwerveDrivetrain drivetrain;
  public final LimelightNode limelight;
  public final QuestNavNode questnav;
  public final Arm arm;
  public final Turret turret;
  public final FlyWheel flywheel;
  public final Intake intake;
  public final Indexer index;
  public final VisualizerNode virtualRobot;
  public final TrajectoryNode trajetorySim;
  public final GamePieceNode gamePieceViz;
  public final Superstructure superstructure;

  public RobotContainer() {

    //WHEELS AHHH
    //GG PAPA
    //GGGGGGGG

    this.driver = ControlsBuilder.buildDriver();
    this.operator = ControlsBuilder.buildOperator();

    this.drivetrain = DrivetrainBuilder.buildModule();

    this.limelight = VisionBuilder.limelightNode(
            () -> drivetrain.getPigeon2().getRotation2d(),
            () -> drivetrain.getPigeon2().getAngularVelocityZWorld().getValueAsDouble(), 
            drivetrain::consumeVisionData
    );

    this.questnav = VisionBuilder.questNode(drivetrain::consumeVisionData);

    this.turret = TurretBuilder.buildModule(drivetrain);
    this.arm = ArmBuilder.buildModule();
    this.intake = IntakeBuilder.buildModule();
    this.index = IndexerBuilder.buildModule();

    this.flywheel = FlywheelBuilder.buildModule();

    this.superstructure = Manifest.SuperstructureBuilder.buildModule(
        this.turret, this.arm, this.intake, this.index, this.flywheel
    );

    this.autoChooser = AutoBuilder.build(KeyManager.AUTOCHOOSER_KEY);

    this.virtualRobot = VisualizerBuilder.buildNode(
      ()-> turret.getDegrees(),
      ()-> arm.getState().position,
      msg -> msg.telemeterize(KeyManager.VISUALIZER_KEY + KeyManager.COMPONENTS_KEY)
    );

    this.trajetorySim = TrajectoryBuilder.buildNode(
      ()-> drivetrain.getState().Pose,
      ()-> turret.getDegrees(),
      ()-> arm.getState().position,
      msg -> msg.telemeterize(KeyManager.VISUALIZER_KEY + KeyManager.TRAJECTORY_KEY)
    );

    this.gamePieceViz = Manifest.GamePieceBuilder.buildNode(
      trajetorySim::getTrajectory,
      ()-> operator.getActionButtons().right().getAsBoolean(),
      msg -> msg.telemeterize(KeyManager.VISUALIZER_KEY + KeyManager.GAMEPIECE_KEY)
    );

    AutoBindings.parameterized(autoChooser, drivetrain, questnav).bind();
    
    DriverBindings.parameterized(drivetrain, driver).bind();

    OperatorBindings.parameterized(operator, turret, arm, flywheel, intake, index, superstructure).bind();

  }

  @Override
  public void updateNodes() {

      if (limelight != null) {
        limelight.periodic();
      }

      if (questnav != null){
        questnav.periodic();
        
      }

      if(virtualRobot != null){
        virtualRobot.periodic();
      }

      if(trajetorySim != null){
        trajetorySim.periodic();
      }

      if (gamePieceViz != null) {
        gamePieceViz.periodic();
      }
  }

  @Override
  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
