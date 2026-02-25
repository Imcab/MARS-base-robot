package frc.robot.configuration.bindings;

import frc.robot.configuration.factories.FlyWheelsRequestFactory;
import frc.robot.configuration.factories.IntakeRequestFactory;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.configuration.advantageScope.visuals.VisualsFactory;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode.GamePieceMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode.TrajectoryMsg;
import mars.source.models.containers.Binding;

import mars.source.operator.ControllerOI;
import mars.source.services.nodes.Node;

public class OperatorBindings implements Binding {

    private final ControllerOI operator;
    private final Superstructure superstructure;
    
    private Turret turret;
    private Arm arm;
    private Intake intake;
    private CommandSwerveDrivetrain drivetrain;
    private FlyWheel flyWheelsIntake;

    private Node<GamePieceMsg> gamePieceViz;
    private Node<TrajectoryMsg> trajectoryNode;

    private OperatorBindings(ControllerOI operator, Superstructure superstructure) {
        this.operator = operator;
        this.superstructure = superstructure;
    }

    public static OperatorBindings create(ControllerOI operator, Superstructure ss) {
        return new OperatorBindings(operator, ss);
    }

    public OperatorBindings withSubsystems(Turret t, Arm a, Intake i, CommandSwerveDrivetrain dt, FlyWheel f) {
        this.turret = t;
        this.arm = a;
        this.intake = i;
        this.drivetrain = dt;
        this.flyWheelsIntake = f;
        return this;
    }

    public OperatorBindings withNodes(Node<GamePieceMsg> gp, Node<TrajectoryMsg> tr) {
        this.gamePieceViz = gp;
        this.trajectoryNode = tr;
        return this;
    }

    @Override
    public void bind() {
        var buttons = operator.getActionButtons();
        var bumpers = operator.getBumpers();
        /* 
        buttons.right().whileTrue(
            superstructure.shootOnTheMove()
                .alongWith(VisualsFactory.triggerShootVisuals(
                    trajectoryNode, 
                    gamePieceViz, 
                    drivetrain, 
                    turret
                ))
        );*/

        bumpers.left().whileTrue(intake.setControl(() -> IntakeRequestFactory.angle.withAngle(140).Tolerance(2).withMode(intakeMODE.kDOWN)));
        bumpers.right().whileTrue(intake.setControl(() -> IntakeRequestFactory.angle.withAngle(0).Tolerance(2).withMode(intakeMODE.kUP)));

        buttons.top().whileTrue(intake.setControl(()-> IntakeRequestFactory.voltage.withVolts(3)));
        buttons.bottom().whileTrue(intake.setControl(()-> IntakeRequestFactory.voltage.withVolts(-3)));

        buttons.right().whileTrue(flyWheelsIntake.setControl(()-> FlyWheelsRequestFactory.voltageRequest.withVolts(-12)));
        
    }
}