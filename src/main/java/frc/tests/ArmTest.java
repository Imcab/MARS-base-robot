package frc.tests;

import com.stzteam.mars.test.MARSTest;
import com.stzteam.mars.test.TestRoutine;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.constants.Constants;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.requests.moduleRequests.ArmRequestFactory;

@MARSTest(name = "Arm PID Motion Test")
public class ArmTest extends TestRoutine{
    private final Arm a;

    public ArmTest(Arm arm){
        this.a = arm;
    }

    @Override
    public Command getRoutineCommand() {
        return Commands.sequence(

            run(ArmRequestFactory.setAngle().withAngle(10).withTolerance(Constants.ARM_TOLERANCE), a),

            waitFor(()-> a.isAtTarget(Constants.ARM_TOLERANCE), 2),

            assertLessThan(
                calculateError(10, a.getState().position), 
                2, 
                "Error es muy alto"
            ),

            delay(1),

            run(ArmRequestFactory.setAngle().withAngle(35).withTolerance(Constants.ARM_TOLERANCE), a),

            waitFor(()-> a.isAtTarget(Constants.ARM_TOLERANCE), 2),

            assertLessThan(
                calculateError(35, a.getState().position), 
                2, 
                "Error es muy alto"
            ),

            delay(1),

            run(ArmRequestFactory.setAngle().withAngle(0).withTolerance(Constants.ARM_TOLERANCE), a),

            waitFor(()-> a.isAtTarget(Constants.ARM_TOLERANCE), 2),

            assertLessThan(
                calculateError(0, a.getState().position), 
                2, 
                "Error es muy alto"
            ),

            delay(0.5),

            run(ArmRequestFactory.idle(), a)

        );

    }


}
