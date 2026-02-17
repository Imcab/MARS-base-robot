package frc.robot.configuration.bindings;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.configuration.constants.ModuleConstants.SwerveConstants;
import frc.robot.configuration.factories.SwerveRequestFactory;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import mars.source.models.containers.Binding;
import mars.source.operator.ControllerOI;

public class DriverBindings implements Binding{

    private final CommandSwerveDrivetrain drivetrain;

    private DriverBindings(CommandSwerveDrivetrain drivetrain) {
        this.drivetrain = drivetrain;
    }

    public static DriverBindings parameterized(CommandSwerveDrivetrain drivetrain) {
        return new DriverBindings(drivetrain);
    }

    @Override
    public void bind(ControllerOI driver) {
        var driverLeftStick = driver.getLeftStick();
        var driverRightStick = driver.getRightStick();
        var driverDPad = driver.getDPadTriggers();
        var driverSystem = driver.getSystemTriggers();
        var driverBumpers = driver.getBumpers();

        drivetrain.setDefaultCommand(
            drivetrain.applyRequest(() ->
                SwerveRequestFactory.driveFieldCentric
                    .withVelocityX(-driverLeftStick.y().getAsDouble() * SwerveConstants.MaxSpeed)
                    .withVelocityY(-driverLeftStick.x().getAsDouble() * SwerveConstants.MaxSpeed)
                    .withRotationalRate(-driverRightStick.x().getAsDouble() * SwerveConstants.MaxAngularRate)
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
                new Rotation2d(-driverLeftStick.y().getAsDouble(), -driverLeftStick.x().getAsDouble())
            )
        ));
    }
}