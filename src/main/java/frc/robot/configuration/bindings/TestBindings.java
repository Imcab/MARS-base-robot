package frc.robot.configuration.bindings;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.tests.IntakeTest;
import mars.source.models.containers.Binding;
import mars.source.test.TestRoutine;

public class TestBindings implements Binding{

    private SendableChooser<TestRoutine> tests = new SendableChooser<>();

    private Turret turret;
    private Arm arm;
    private Intake intake;
    private CommandSwerveDrivetrain drivetrain;
    private FlyWheel flyWheelsIntake;

    private TestBindings(Intake intake){
        this.intake = intake;
    }

    public static TestBindings create(Intake intake){
        return new TestBindings(intake);
    }

    @Override
    public void bind() {
        tests.addOption("IntakeTest", new IntakeTest(intake));

        SmartDashboard.putData("TestTables", tests );
    }

    public TestRoutine getSelected(){
        return tests.getSelected();
    }
    
}
