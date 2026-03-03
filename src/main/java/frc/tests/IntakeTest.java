package frc.tests;

import com.stzteam.mars.test.MARSTest;
import com.stzteam.mars.test.TestRoutine;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;
import frc.robot.core.requests.moduleRequests.IntakeRequestFactory;

@MARSTest(name = "Intake Diagnostic Routine")
public class IntakeTest extends TestRoutine {

    private final Intake intake;

    public IntakeTest(Intake intake) {
        this.intake = intake;
    }

    @Override
    public Command getRoutineCommand() {
        return Commands.sequence(
            
            run(IntakeRequestFactory.setAngle.withAngle(-130).Tolerance(2).withMode(intakeMODE.kDOWN), intake),

            waitFor(() -> intake.isAtTarget(2), 2.0),
            
            assertLessThan(
                () -> Math.abs(-130 - intake.getState().position), 
                2, 
                "El error del Intake es muy alto al llegar al target"
            ),

            delay(1),

            run(IntakeRequestFactory.setAngle.withAngle(-10).Tolerance(2).withMode(intakeMODE.kUP), intake),

            waitFor(() -> intake.isAtTarget(2), 2.0),

            assertLessThan(
                () -> Math.abs(-10 - intake.getState().position), 
                2, 
                "El error del Intake es muy alto al llegar al target"
            ),

            delay(0.5),

            run(IntakeRequestFactory.idle, intake)
             
            
        );
    }
}