package frc.tests;

import com.stzteam.mars.test.MARSTest;
import com.stzteam.mars.test.TestRoutine;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.constants.Constants;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.requests.moduleRequests.TurretRequestFactory;

@MARSTest(name = "Turret Angle Test")
public class TurretTest extends TestRoutine {

    private final Turret t;

    private Rotation2d target1 = new Rotation2d(Math.toRadians(45));
    private Rotation2d target2 = new Rotation2d(Math.toRadians(-45));

    public TurretTest(Turret turret){
        this.t = turret;
    }

    @Override
    public Command getRoutineCommand() {
        return Commands.sequence(
                
            run(TurretRequestFactory.position()
                .withTargetAngle(target1)
                .withTolerance(Constants.TURRET_TOLERANCE), t),
   
            waitFor(() -> t.isAtTarget(Constants.TURRET_TOLERANCE), 2.0),

            assertLessThan(
                () -> Math.abs(target1.minus(t.getState().angle).getRadians()), 
                2.0, 
                "High turret error on target 1"
            ),

            delay(1.0),

            run(TurretRequestFactory.position()
                .withTargetAngle(target2)
                .withTolerance(Constants.TURRET_TOLERANCE), t),

            waitFor(() -> t.isAtTarget(Constants.TURRET_TOLERANCE), 2.0),

            assertLessThan(
                () -> Math.abs(target2.minus(t.getState().angle).getRadians()), 
                2.0, 
                "High turret error on target 2"
            ),

            delay(1),

            run(TurretRequestFactory.position().withTargetAngle(Rotation2d.kZero), t),

            waitFor(() -> t.isAtTarget(Constants.TURRET_TOLERANCE), 2.0),

            assertLessThan(
                () -> Math.abs(target2.minus(t.getState().angle).getRadians()), 
                2.0, 
                "High turret error on zeroed"
            ),

            delay(0.5),

           run(TurretRequestFactory.idle(), t)

        );
    }
}