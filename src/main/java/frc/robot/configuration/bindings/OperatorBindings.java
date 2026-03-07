package frc.robot.configuration.bindings;

import com.stzteam.mars.models.containers.Binding;
import com.stzteam.mars.operator.ControllerOI;
import com.stzteam.mars.services.nodes.Node;

import frc.robot.core.modules.superstructure.composite.Superstructure;

import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode.GamePieceMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode.TrajectoryMsg;

public class OperatorBindings implements Binding {

    private final ControllerOI operator;
    private final Superstructure superstructure;

    private Node<GamePieceMsg> gamePieceViz;
    private Node<TrajectoryMsg> trajectoryNode;

    private OperatorBindings(ControllerOI operator, Superstructure superstructure) {
        this.operator = operator;
        this.superstructure = superstructure;
    }

    public static OperatorBindings create(ControllerOI operator, Superstructure ss) {
        return new OperatorBindings(operator, ss);
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
        //var driverSystem = operator.getSystemTriggers();
        var triggers = operator.getAnalogTriggers();
        
        // --------------------------------------------------------------- MANDO
        // ---------------------------------------------------------------

        // ----- Botones (a,b,x,y) -----
        /* 
        buttons.bottom().whileTrue(intake.setControl(()-> IntakeRequestFactory.setAngle() //Bajar el intake (a)
        .withAngle(-130) 
        .Tolerance(Constants.INTAKE_TOLERANCE)
        .withMode(intakeMODE.kDOWN)));

        buttons.top().whileTrue(intake.setControl(()-> IntakeRequestFactory.setAngle() //Bubir el intake (y)
        .withAngle(-10)
        .Tolerance(Constants.INTAKE_TOLERANCE)
        .withMode(intakeMODE.kUP)));    */   

        buttons.right().whileTrue(superstructure.eatCommand()); //Comer fuels

        buttons.left().whileTrue(superstructure.clearFuel()); // Desatorar fuels
        // ----- Botones (a,b,x,y) -----

        bumpers.left().whileTrue(superstructure.lockToHub()); 
        

        //triggers.right().whileTrue(superstructure.ShootAngleTest(()-> superstructure.getAngle(), ()-> superstructure.getRPM()));
        triggers.right().whileTrue(superstructure.shootOnTheMove());
        
        //driverSystem.start().toggleOnTrue(intake.setControl(()-> IntakeRequestFactory.setAngle())); //Resetea la posición del encoder a 0 (start)}
        // --------------------------------------------------------------- MANDO ---------------------------------------------------------------

    }
}