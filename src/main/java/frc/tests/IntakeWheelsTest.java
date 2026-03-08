package frc.tests;

import com.stzteam.mars.test.MARSTest;
import com.stzteam.mars.test.TestRoutine;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.requests.moduleRequests.FlyWheelRequestFactory;

@MARSTest(name = "IntakeWheels Test")
public class IntakeWheelsTest extends TestRoutine{
    FlyWheel f;

    public IntakeWheelsTest(FlyWheel f){
        this. f = f;
    }

    @Override
    public Command getRoutineCommand(){

        return Commands.sequence(
            f.setControl(()-> FlyWheelRequestFactory.moveVoltage().withVolts(12)),

            delay(3),

            f.setControl(()-> FlyWheelRequestFactory.idleIntake()),

            delay(1.5),

            f.setControl(()-> FlyWheelRequestFactory.moveVoltage().withVolts(-12)),

            delay(3),

            f.setControl(()-> FlyWheelRequestFactory.idleIntake())

        );

    }
}
