package frc.robot.configuration.bindings;

import frc.robot.configuration.constants.Constants;
import frc.robot.configuration.factories.ArmRequestFactory;
import frc.robot.configuration.factories.FlyWheelsRequestFactory;
import frc.robot.configuration.factories.IndexerRequestFactory;
import frc.robot.configuration.factories.IntakeRequestFactory;
import frc.robot.configuration.factories.TurretRequestFactory;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import mars.source.models.containers.Binding;
import mars.source.operator.ControllerOI;

public class OperatorBindings implements Binding{

    private final ControllerOI operator;
    private final Turret turret;
    private final Arm arm;
    private final FlyWheel flywheel;
    private final Intake intake;
    private final Indexer index;
    private final Superstructure superstructure;
 
    private OperatorBindings(ControllerOI operator,Turret turret, Arm arm, FlyWheel flywheel, Intake intake, Indexer index, Superstructure superstructure){
        this.operator = operator;
        this.turret = turret;
        this.arm = arm;
        this.flywheel = flywheel;
        this.intake = intake;
        this.index = index;
        this.superstructure = superstructure;
    }

    public static OperatorBindings parameterized(ControllerOI operator, Turret turret, Arm arm, FlyWheel flywheel, Intake intake, Indexer index, Superstructure superstructure){
        return new OperatorBindings(operator, turret, arm, flywheel, intake, index, superstructure);
    }

    @Override
    public void bind() {

        var operatorButtons = operator.getActionButtons();
        //var operatorDPad = operator.getDPadTriggers();
        //var operatorSystem = operator.getSystemTriggers();
        var operatorBumpers = operator.getBumpers();


        operatorButtons.right().whileTrue(superstructure.shootOnTheMove());
        //operatorButtons.bottom().whileTrue(arm.setControl(()-> ArmRequestFactory.interpolate.withTolerance(3).withDistance(()-> turret.distanceTo(Constants.HUB_LOCATION.toTranslation2d()))));

        operatorBumpers.right().whileTrue(intake.setControl(()-> IntakeRequestFactory.angle.withAngle(90).Tolerance(2)));
        operatorBumpers.left().whileTrue(intake.setControl(()-> IntakeRequestFactory.angle.withAngle(0).Tolerance(2)));

        //operatorButtons.bottom().whileTrue(turret.runRequest(()-> TurretRequestFactory.voltage.withVolts(6)));
        //operatorButtons.right().whileTrue(turret.runRequest(()-> TurretRequestFactory.voltage.withVolts(-6)));

        //operatorButtons.bottom().whileTrue(arm.setControl(()-> ArmRequestFactory.angle.withAngle(50). withTolerance(5)));
        
        //operatorButtons.right().whileTrue(index.runRequest(()-> IndexerRequestFactory.voltage.withVolts(12)));

        //operatorBumpers.right().whileTrue(arm.setControl(()-> ArmRequestFactory.voltage.withVolts(-12)));

    }
    
}
