package frc.robot.configuration.bindings;

import com.stzteam.mars.models.containers.Binding;
import com.stzteam.mars.test.TestRoutine;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.tests.ArmTest;
import frc.tests.IndexerTest;
import frc.tests.IntakeTest;
import frc.tests.InterpolateTest;
import frc.tests.TurretTest;


public class TestBindings implements Binding{

    private SendableChooser<TestRoutine> tests = new SendableChooser<>();

    private Turret turret;
    private Arm arm;
    private Intake intake;
    private CommandSwerveDrivetrain drivetrain;
    private FlyWheel flyWheelsIntake;
    private Indexer index;
    private Superstructure s;

    private TestBindings(Intake intake, Turret turret, Arm arm, Indexer index, Superstructure sup){
        this.intake = intake;
        this.turret = turret;
        this.arm = arm;
        this.index = index;
        this.s = sup;
    }

    public static TestBindings create(Intake intake, Turret turret, Arm arm, Indexer index, Superstructure sup){
        return new TestBindings(intake, turret, arm, index, sup);
    }

    @Override
    public void bind() {
        tests.addOption("IntakeTest", new IntakeTest(intake));
        tests.addOption("TurretTest", new TurretTest(turret));
        tests.addOption("ArmTest", new ArmTest(arm));
        tests.addOption("IndexTest", new IndexerTest(index));
        tests.addOption("InterTest", new InterpolateTest(s));

        SmartDashboard.putData("TestRoutines", tests);
    }

    public TestRoutine getSelected(){
        return tests.getSelected();
    }
    
}
