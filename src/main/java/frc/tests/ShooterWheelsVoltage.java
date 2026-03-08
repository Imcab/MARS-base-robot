package frc.tests;

import com.stzteam.mars.test.MARSTest;
import com.stzteam.mars.test.TestRoutine;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.requests.moduleRequests.FlyWheelRequestFactory;

@MARSTest(name="ShooterVoltage Test")
public class ShooterWheelsVoltage extends TestRoutine{
    FlyWheel wv;

    public ShooterWheelsVoltage (FlyWheel wv){
        this. wv = wv;
    }

    @ Override
    public Command getRoutineCommand(){

        return Commands.sequence(

            wv.setControl(()->FlyWheelRequestFactory.moveVoltage().withVolts(12)),

            delay(4),

            wv.setControl(()->FlyWheelRequestFactory.idleOutake()),

            delay(2),

            wv.setControl(()->FlyWheelRequestFactory.moveVoltage().withVolts(-12)),

            delay(4),

            wv.setControl(()->FlyWheelRequestFactory.idleOutake())
        
        );

       
    }
    
}
