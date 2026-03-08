package frc.robot.configuration.bindings;

import com.stzteam.mars.models.containers.Binding;
import com.stzteam.mars.test.TestRoutine;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.tests.ArmTest;
import frc.tests.IndexerTest;
import frc.tests.IntakeTest;
import frc.tests.IntakeWheelsTest;
import frc.tests.InterpolateTest;
import frc.tests.ShooterWheelsVoltage;
import frc.tests.TurretTest;


public class TestBindings implements Binding{

    private SendableChooser<TestRoutine> tests = new SendableChooser<>();


    private Superstructure s;

    private TestBindings(Superstructure sup){
        this.s = sup;
    }

    public static TestBindings create( Superstructure sup){
        return new TestBindings( sup);
    }

    @Override
    public void bind() {

        tests.addOption("IntakeTest", new IntakeTest(s.getIntake()));
        tests.addOption("TurretTest", new TurretTest(s.getTurret()));
        tests.addOption("ArmTest", new ArmTest(s.getArm()));
        tests.addOption("IndexTest", new IndexerTest(s.getIndexer()));
        tests.addOption("InterTest", new InterpolateTest(s));
        tests.addOption("IntakeWheels Test", new IntakeWheelsTest(s.getFlyWheelsIntake()));
        tests.addOption("ShooterVoltage Test", new ShooterWheelsVoltage(s.getFlywheelShooter()));

        SmartDashboard.putData("TestRoutines", tests);
    }

    public TestRoutine getSelected(){
        return tests.getSelected();
    }
    
}
