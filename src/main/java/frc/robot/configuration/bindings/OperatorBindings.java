package frc.robot.configuration.bindings;

import frc.robot.configuration.KeyManager;
import frc.robot.configuration.factories.FlyWheelsRequestFactory;
import frc.robot.configuration.factories.IntakeRequestFactory;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode.GamePieceMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode.TrajectoryMsg;
import mars.source.models.containers.Binding;
import mars.source.operator.ControllerOI;
import mars.source.services.nodes.Node;
import mars.source.utils.TerminalBooter; // ✨ Importamos el motor de la terminal

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
        var driverSystem = operator.getSystemTriggers();
        
        var intakeDown = IntakeRequestFactory.angle.withAngle(-130).Tolerance(2).withMode(intakeMODE.kDOWN);
        var intakeUp = IntakeRequestFactory.angle.withAngle(-10).Tolerance(2).withMode(intakeMODE.kUP);
        var intakeOuttake = IntakeRequestFactory.voltage.withVolts(0.44);
        var intakeIntake = IntakeRequestFactory.voltage.withVolts(-3);
        var flyWheelsShoot = FlyWheelsRequestFactory.voltageRequest.withVolts(-11);

        // --------------------------------------------------------------- MANDO ---------------------------------------------------------------

        buttons.bottom().whileTrue(intake.setControl(()-> IntakeRequestFactory.angle //Bajar el intake (a)
        .withAngle(-130) 
        .Tolerance(2)
        .withMode(intakeMODE.kDOWN)));

        buttons.top().whileTrue(intake.setControl(()-> IntakeRequestFactory.angle //Bubir el intake (y)
        .withAngle(-10)
        .Tolerance(2)
        .withMode(intakeMODE.kUP)));
        
        buttons.right().whileTrue(flyWheelsIntake.setControl(() -> FlyWheelsRequestFactory.voltageRequest
        .withVolts(-11))); //Ruedas intake (b)

        driverSystem.start().toggleOnTrue(intake.setControl(()-> IntakeRequestFactory.reset)); //Resetea la posición del encoder a 0 (start)


        // --------------------------------------------------------------- MANDO ---------------------------------------------------------------


        //  REGISTRO EN LA TERMINAL (MARS GCS)
        TerminalBooter.registerRemoteRequest(KeyManager.INTAKE_KEY, "Down", intakeDown);
        TerminalBooter.registerRemoteRequest(KeyManager.INTAKE_KEY, "Up", intakeUp);
        TerminalBooter.registerRemoteRequest(KeyManager.INTAKE_KEY, "Outtake", intakeOuttake);
        TerminalBooter.registerRemoteRequest(KeyManager.INTAKE_KEY, "Intake", intakeIntake);
        TerminalBooter.registerRemoteRequest(KeyManager.INTAKE_KEY, "Idle", IntakeRequestFactory.idle);
        TerminalBooter.registerRemoteRequest(KeyManager.FLYWHEEL_KEY, "Shoot", flyWheelsShoot);
        TerminalBooter.registerRemoteRequest(KeyManager.FLYWHEEL_KEY, "Idle", FlyWheelsRequestFactory.Idle);
    }
}