package frc.robot.configuration.bindings;

import com.stzteam.mars.models.containers.Binding;
import com.stzteam.mars.operator.ControllerOI;
import com.stzteam.mars.services.nodes.Node;
import com.stzteam.mars.utils.TerminalGCS;

import frc.robot.configuration.KeyManager;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.requests.moduleRequests.FlyWheelRequestFactory;
import frc.robot.core.requests.moduleRequests.IntakeRequestFactory;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode.GamePieceMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode.TrajectoryMsg;
import frc.robot.configuration.constants.Constants;

public class OperatorBindings implements Binding {

    private final ControllerOI operator;
    private final Superstructure superstructure;

    private Turret turret;
    private Arm arm;
    private Intake intake;
    private CommandSwerveDrivetrain drivetrain;
    private FlyWheel flyWheelsIntake;
    private FlyWheel flywheelShooter;

    private Node<GamePieceMsg> gamePieceViz;
    private Node<TrajectoryMsg> trajectoryNode;

    private OperatorBindings(ControllerOI operator, Superstructure superstructure) {
        this.operator = operator;
        this.superstructure = superstructure;
    }

    public static OperatorBindings create(ControllerOI operator, Superstructure ss) {
        return new OperatorBindings(operator, ss);
    }

    public OperatorBindings withSubsystems(Turret t, Arm a, Intake i, CommandSwerveDrivetrain dt, FlyWheel f,
            FlyWheel fs) {
        this.turret = t;
        this.arm = a;
        this.intake = i;
        this.drivetrain = dt;
        this.flywheelShooter = fs;
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

        var intakeDown = IntakeRequestFactory.setAngle().withAngle(-130).Tolerance(2).withMode(intakeMODE.kDOWN);
        var intakeUp = IntakeRequestFactory.setAngle().withAngle(-10).Tolerance(2).withMode(intakeMODE.kUP);
        var intakeOuttake = IntakeRequestFactory.moveVoltage().withVolts(0.44);
        var intakeIntake = IntakeRequestFactory.moveVoltage().withVolts(-3);
        var flyWheelsShoot = FlyWheelRequestFactory.moveVoltage().withVolts(-11);

        // --------------------------------------------------------------- MANDO
        // ---------------------------------------------------------------

        buttons.bottom().whileTrue(intake.setControl(() -> IntakeRequestFactory.setAngle() // Bajar el intake (a)
                .withAngle(-130)
                .Tolerance(Constants.INTAKE_TOLERANCE)
                .withMode(intakeMODE.kDOWN)));

        buttons.top().whileTrue(intake.setControl(() -> IntakeRequestFactory.setAngle() // Bubir el intake (y)
                .withAngle(-10)
                .Tolerance(Constants.INTAKE_TOLERANCE)
                .withMode(intakeMODE.kUP)));

        buttons.right().whileTrue(flyWheelsIntake.setControl(() -> FlyWheelRequestFactory.moveVoltage()
                .withVolts(-9))); // Ruedas intake (b)

        bumpers.left().whileTrue(superstructure.lockToHub());

        bumpers.right().whileTrue(
                superstructure.ShootAngleTest(() -> superstructure.getAngle(), () -> superstructure.getRPM()));

        // bumpers.left().whileTrue(superstructure.ShootAngle(0, -4000));

        driverSystem.start().toggleOnTrue(intake.setControl(() -> IntakeRequestFactory.setAngle())); // Resetea la
                                                                                                     // posición del
                                                                                                     // encoder a 0
                                                                                                     // (start)}
        // --------------------------------------------------------------- MANDO
        // ---------------------------------------------------------------

        // REGISTRO EN LA TERMINAL (MARS GCS)
        TerminalGCS.registerRemoteRequest(KeyManager.INTAKE_KEY, "Down", intakeDown);
        TerminalGCS.registerRemoteRequest(KeyManager.INTAKE_KEY, "Up", intakeUp);
        TerminalGCS.registerRemoteRequest(KeyManager.INTAKE_KEY, "Outtake", intakeOuttake);
        TerminalGCS.registerRemoteRequest(KeyManager.INTAKE_KEY, "Intake", intakeIntake);
        TerminalGCS.registerRemoteRequest(KeyManager.INTAKE_KEY, "Idle", IntakeRequestFactory.idle());
        TerminalGCS.registerRemoteRequest(KeyManager.FLYWHEEL_INTAKE_KEY, "Shoot", flyWheelsShoot);
        TerminalGCS.registerRemoteRequest(KeyManager.FLYWHEEL_INTAKE_KEY, "Idle", FlyWheelRequestFactory.idle());

    }
}