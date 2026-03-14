// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.bindings;

import com.stzteam.mars.models.containers.Binding;
import com.stzteam.mars.operator.ControllerOI;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.configuration.constants.ModuleConstants.SwerveConstants;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.modules.swerve.SwerveRequestFactory;

public class DriverBindings implements Binding {

  private final CommandSwerveDrivetrain drivetrain;

  private final ControllerOI driver;

  public double alllianceFactor;

  private DriverBindings(CommandSwerveDrivetrain drivetrain, ControllerOI driver) {
    this.drivetrain = drivetrain;
    this.driver = driver;
  }

  public static DriverBindings create(CommandSwerveDrivetrain drivetrain, ControllerOI driver) {
    return new DriverBindings(drivetrain, driver);
  }

  @Override
  public void bind() {
    var driverLeftStick = driver.getLeftStick();
    var driverRightStick = driver.getRightStick();
    // var driverDPad = driver.getDPadTriggers();
    // var driverSystem = driver.getSystemTriggers();
    var driverBumpers = driver.getBumpers();
    var driverButtons = driver.getActionButtons();

    drivetrain.setDefaultCommand(
        drivetrain.applyRequest(
            () ->
                SwerveRequestFactory.driveFieldCentric()
                    .withVelocityX(
                        driverLeftStick.y().getAsDouble() // TODO: Rojo Positivo - Azul Negativo
                            * SwerveConstants.MaxSpeed
                            * (driverBumpers.right().getAsBoolean() ? 0.5 : 1.0)
                            * (driverBumpers.left().getAsBoolean() ? 0.2 : 1.0))
                    .withVelocityY(
                        driverLeftStick.x().getAsDouble() // TODO: Rojo Positivo - Azul Negativo
                            * SwerveConstants.MaxSpeed
                            * (driverBumpers.right().getAsBoolean() ? 0.5 : 1.0)
                            * (driverBumpers.left().getAsBoolean() ? 0.2 : 1.0))
                    .withRotationalRate(
                        -driverRightStick.x().getAsDouble() * SwerveConstants.MaxAngularRate)));

    driverButtons.top().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

    driverButtons.bottom().whileTrue(drivetrain.applyRequest(() -> SwerveRequestFactory.brake()));

    driverButtons
        .left()
        .whileTrue(drivetrain.getPoseFinder().toPose(new Pose2d(0.579, 0.579, Rotation2d.kZero)));

    driverButtons
        .right()
        .whileTrue(
            drivetrain.getPoseFinder().toPose(new Pose2d(3.559, 2.766, Rotation2d.kCCW_90deg)));
  }
}
