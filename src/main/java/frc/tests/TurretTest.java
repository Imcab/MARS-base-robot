package frc.tests;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.constants.Constants;
import frc.robot.configuration.factories.TurretRequestFactory;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import mars.source.test.MARSTest;
import mars.source.test.TestRoutine;

@MARSTest(name = "Turret Angle Test")
public class TurretTest extends TestRoutine {

    private final Turret t;

    private Rotation2d target1 = new Rotation2d(Math.toRadians(50));
    private Rotation2d target2 = new Rotation2d(Math.toRadians(-50));

    public TurretTest(Turret turret){
        this.t = turret;
    }

    @Override
    public Command getRoutineCommand() {
        return Commands.sequence(
                
            Commands.runOnce(() -> t.setRequest(
                TurretRequestFactory.toAngle
                .withTargetAngle(target1)
                .withTolerance(Constants.TURRET_TOLERANCE)
            ), t),

            waitFor(() -> t.isAtTarget(Constants.TURRET_TOLERANCE), 2.0),

            assertLessThan(
                () -> Math.abs(target1.minus(t.getState().angle).getRadians()), 
                2.0, 
                "High turret error en target 1"
            ),

            delay(1.0),

            Commands.runOnce(() -> t.setRequest(
                TurretRequestFactory.toAngle
                .withTargetAngle(target2)
                .withTolerance(Constants.TURRET_TOLERANCE)
            ), t),

            waitFor(() -> t.isAtTarget(Constants.TURRET_TOLERANCE), 2.0),

            assertLessThan(
                () -> Math.abs(target2.minus(t.getState().angle).getRadians()), 
                2.0, 
                "High turret error en target 2"
            ),

            delay(1),

            Commands.runOnce(() -> t.setRequest(
                TurretRequestFactory.toAngle
                .withTargetAngle(Rotation2d.kZero)
                .withTolerance(Constants.TURRET_TOLERANCE)
            ), t),

            waitFor(() -> t.isAtTarget(Constants.TURRET_TOLERANCE), 2.0),

            assertLessThan(
                () -> Math.abs(target2.minus(t.getState().angle).getRadians()), 
                2.0, 
                "High turret error en target 2"
            ),

            delay(0.5),

            Commands.runOnce(() -> t.setRequest(TurretRequestFactory.idle), t)

        );
    }
}