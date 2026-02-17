// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.Manifest.ArmBuilder;
import frc.robot.configuration.Manifest.ControlsBuilder;
import frc.robot.configuration.Manifest.DrivetrainBuilder;
import frc.robot.configuration.Manifest.TurretBuilder;
import frc.robot.configuration.Manifest.VisionBuilder;
import frc.robot.configuration.constants.SwerveConstants;
import frc.robot.configuration.factories.SwerveRequestFactory;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.modules.swerve.nodes.LimelightNode;
import frc.robot.core.modules.swerve.nodes.QuestNavNode;
import mars.source.operator.ControllerOI;

public class RobotContainer {

  public final ControllerOI driver;
  public final ControllerOI operator;

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

    configureBindings();
  }

  private void configureBindings() {

      var driverLeftStick = driver.getLeftStick();
      var driverRightStick = driver.getRightStick();
      var driverDPad = driver.getDPadTriggers();
      var driverSystem = driver.getSystemTriggers();
      var driverBumpers = driver.getBumpers();
      var driverActions = driver.getActionButtons();
  
      drivetrain.setDefaultCommand(
          drivetrain.applyRequest(() ->
              SwerveRequestFactory.driveFieldCentric
                  .withVelocityX(-driverLeftStick.y().getAsDouble() * SwerveConstants.MaxSpeed) // Avanzar
                  .withVelocityY(-driverLeftStick.x().getAsDouble() * SwerveConstants.MaxSpeed) // Strafe
                  .withRotationalRate(-driverRightStick.x().getAsDouble() * SwerveConstants.MaxAngularRate) // Girar
          )
      );

      driverDPad.up().whileTrue(CommandSwerveDrivetrain.moveXCommand(drivetrain, SwerveConstants.crossMovementSpeed));
      driverDPad.down().whileTrue(CommandSwerveDrivetrain.moveXCommand(drivetrain, -SwerveConstants.crossMovementSpeed));
      driverDPad.left().whileTrue(CommandSwerveDrivetrain.moveYCommand(drivetrain, SwerveConstants.crossMovementSpeed));
      driverDPad.right().whileTrue(CommandSwerveDrivetrain.moveYCommand(drivetrain, -SwerveConstants.crossMovementSpeed));

      driverSystem.start().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

      driverBumpers.right().whileTrue(drivetrain.applyRequest(() -> SwerveRequestFactory.brake));

      driverBumpers.left().whileTrue(drivetrain.applyRequest(() -> 
          SwerveRequestFactory.point.withModuleDirection(
              new Rotation2d(
                  -driverLeftStick.y().getAsDouble(), 
                  -driverLeftStick.x().getAsDouble()
              )
          )
      ));

      driverActions.left().whileTrue(
          drivetrain.getPoseFinder().toPose(new Pose2d(1.25, 3.3, Rotation2d.kZero))
      );

  }

  public void updateSensorNodes() {
        if (limelight != null) {
            limelight.periodic();
        }

        if (questnav != null){
          questnav.periodic();
        
        }
    }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
