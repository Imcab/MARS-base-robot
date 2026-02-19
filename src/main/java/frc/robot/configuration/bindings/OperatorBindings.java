package frc.robot.configuration.bindings;

import frc.robot.configuration.constants.Constants;
import frc.robot.configuration.factories.ArmRequestFactory;
import frc.robot.configuration.factories.FlyWheelsRequestFactory;
import frc.robot.configuration.factories.TurretRequestFactory;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import mars.source.models.containers.Binding;
import mars.source.operator.ControllerOI;

public class OperatorBindings implements Binding{

    private final ControllerOI operator;
    private final Turret turret;
    private final Arm arm;
    private final FlyWheel flywheel;
 
    private OperatorBindings(ControllerOI operator,Turret turret, Arm arm, FlyWheel flywheel){
        this.operator = operator;
        this.turret = turret;
        this.arm = arm;
        this.flywheel = flywheel;
    }

    public static OperatorBindings parameterized(ControllerOI operator, Turret turret, Arm arm, FlyWheel flywheel){
        return new OperatorBindings(operator, turret, arm, flywheel);
    }

    @Override
    public void bind() {

        var operatorButtons = operator.getActionButtons();
        //var operatorDPad = operator.getDPadTriggers();
        //var operatorSystem = operator.getSystemTriggers();
        var operatorBumpers = operator.getBumpers();

        operatorButtons.right().whileTrue(turret.setControl(()-> TurretRequestFactory.lockToHub.withTolerance(4)));
        operatorButtons.bottom().whileTrue(arm.setControl(()-> ArmRequestFactory.interpolate.withTolerance(3).withDistance(()-> turret.distanceTo(Constants.HUB_LOCATION.toTranslation2d()))));
        
        //operatorButtons.bottom().whileTrue(turret.runRequest(()-> TurretRequestFactory.voltage.withVolts(6)));
        //operatorButtons.right().whileTrue(turret.runRequest(()-> TurretRequestFactory.voltage.withVolts(-6)));

        //operatorButtons.bottom().whileTrue(arm.setControl(()-> ArmRequestFactory.angle.withAngle(50). withTolerance(5)));

        //operatorButtons.right().whileTrue(flywheel.runRequest(()-> FlyWheelsRequestFactory.RPMRequest.toRPM(3600)));

        //operatorBumpers.right().whileTrue(arm.setControl(()-> ArmRequestFactory.voltage.withVolts(-12)));

    

    }
    
}
