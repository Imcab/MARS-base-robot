// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.stzteam.forgemini.io.SmartChooser;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.KeyManager;
import frc.robot.configuration.Manifest.ArmBuilder;
import frc.robot.configuration.Manifest.AutoBuilder;
import frc.robot.configuration.Manifest.ControlsBuilder;
import frc.robot.configuration.Manifest.DrivetrainBuilder;
import frc.robot.configuration.Manifest.TurretBuilder;
import frc.robot.configuration.Manifest.VisionBuilder;
import frc.robot.configuration.bindings.AutoBindings;
import frc.robot.configuration.bindings.DriverBindings;
import frc.robot.configuration.bindings.OperatorBindings;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
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

  public RobotContainer() {

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

    this.autoChooser = AutoBuilder.build(KeyManager.AUTOCHOOSER_KEY);

    AutoBindings.parameterized(autoChooser, drivetrain, questnav).bind();
    
    DriverBindings.parameterized(drivetrain, driver).bind();

    OperatorBindings.parameterized(operator, turret, arm).bind();

  }

  @Override
  public void updateNodes() {

      if (limelight != null) {
        limelight.periodic();
      }

      if (questnav != null){
        questnav.periodic();
        
      }
  }

  @Override
  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
