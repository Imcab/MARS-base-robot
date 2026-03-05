package frc.tests;

import com.stzteam.mars.test.MARSTest;
import com.stzteam.mars.test.TestRoutine;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.core.modules.superstructure.composite.Superstructure;

@MARSTest(name = "InterpolateTarget")
public class InterpolateTest extends TestRoutine{
    private final Superstructure s;

    public InterpolateTest(Superstructure s){
        this.s = s;
    }

    @Override
    public Command getRoutineCommand() {
        return Commands.sequence(

            run(()-> s.ShootAngle(s.getAngle(), s.getRPM()), s),

            delay(6),

            run(()-> s.stopAll())
            
        );
    }


    
}


