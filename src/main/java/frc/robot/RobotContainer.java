// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.Manifest.ArmBuilder;
import frc.robot.configuration.Manifest.TurretBuilder;
import frc.robot.configuration.Manifest.VisionBuilder;
import frc.robot.configuration.constants.TunerConstants;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.modules.swerve.nodes.LimelightNode;

public class RobotContainer {

  public final CommandSwerveDrivetrain drivetrain;
  public final LimelightNode limelightFront;
  public final Arm arm;
  public final Turret turret;

  public RobotContainer() {

    this.drivetrain = TunerConstants.createDrivetrain();

    this.limelightFront = VisionBuilder.buildLimelightNode(
            () -> drivetrain.getPigeon2().getRotation2d(),
            () -> drivetrain.getPigeon2().getAngularVelocityZWorld().getValueAsDouble(), 
            drivetrain::consumeVisionData
        );

    this.turret = TurretBuilder.buildModule(drivetrain);
    this.arm = ArmBuilder.buildModule();

    configureBindings();
  }

  private void configureBindings() {}

  public void updateSensorNodes() {
        if (limelightFront != null) {
            limelightFront.periodic();
        }
    }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
