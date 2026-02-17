package frc.robot.configuration.bindings;

import frc.robot.configuration.factories.ArmRequestFactory;
import frc.robot.configuration.factories.TurretRequestFactory;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import mars.source.models.containers.Binding;
import mars.source.operator.ControllerOI;

public class OperatorBindings implements Binding{

    private final ControllerOI operator;
    private final Turret turret;
    private final Arm arm;

    private OperatorBindings(ControllerOI operator, Turret turret, Arm arm){
        this.operator = operator;
        this.turret = turret;
        this.arm = arm;
    }

    public static OperatorBindings parameterized(ControllerOI operator, Turret turret, Arm arm){
        return new OperatorBindings(operator, turret, arm);
    }

    @Override
    public void bind() {

        var operatorButtons = operator.getActionButtons();
        //var operatorDPad = operator.getDPadTriggers();
        //var operatorSystem = operator.getSystemTriggers();
        var operatorBumpers = operator.getBumpers();

        operatorButtons.right().whileTrue(turret.setControl(()-> TurretRequestFactory.lockToHub));
        operatorButtons.bottom().whileTrue(arm.setControl(()-> ArmRequestFactory.angle.withAngle(50). withTolerance(5)));

        operatorBumpers.right().whileTrue(arm.setControl(()-> ArmRequestFactory.voltage.withVolts(-12)));


    }
    
}
