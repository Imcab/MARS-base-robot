package frc.robot.configuration.bindings;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.configuration.constants.ModuleConstants.SwerveConstants;
import frc.robot.configuration.factories.SwerveRequestFactory;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import mars.source.models.containers.Binding;
import mars.source.operator.ControllerOI;

public class DriverBindings implements Binding{

    private final CommandSwerveDrivetrain drivetrain;

    private final ControllerOI driver;

    private DriverBindings(CommandSwerveDrivetrain drivetrain, ControllerOI driver) {
        this.drivetrain = drivetrain;
        this.driver = driver;
    }

    public static DriverBindings parameterized(CommandSwerveDrivetrain drivetrain, ControllerOI driver) {
        return new DriverBindings(drivetrain, driver);
    }

    @Override
    public void bind() {
        var driverLeftStick = driver.getLeftStick();
        var driverRightStick = driver.getRightStick();
        var driverDPad = driver.getDPadTriggers();
        var driverSystem = driver.getSystemTriggers();
        var driverBumpers = driver.getBumpers();
        var driverButtons = driver.getActionButtons();

        drivetrain.setDefaultCommand(
            drivetrain.applyRequest(() ->
                SwerveRequestFactory.driveFieldCentric
                    .withVelocityX(-driverLeftStick.y().getAsDouble() * SwerveConstants.MaxSpeed * (driverBumpers.right().getAsBoolean() ? 0.3 : 1.0)) 
                    .withVelocityY(-driverLeftStick.x().getAsDouble() * SwerveConstants.MaxSpeed * (driverBumpers.right().getAsBoolean() ? 0.3 : 1.0)) 
                    .withRotationalRate(-driverRightStick.x().getAsDouble() * SwerveConstants.MaxAngularRate) 
            )
        );

        driverButtons.top().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

        driverBumpers.left().whileTrue(drivetrain.applyRequest(() -> SwerveRequestFactory.brake));

        driverButtons.left().whileTrue(drivetrain.applyRequest(() -> 
            SwerveRequestFactory.point.withModuleDirection(
                new Rotation2d(-driverLeftStick.y().getAsDouble(), -driverLeftStick.x().getAsDouble())
            )
        ));
    }
}