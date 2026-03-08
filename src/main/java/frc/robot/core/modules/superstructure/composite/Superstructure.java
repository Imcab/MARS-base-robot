package frc.robot.core.modules.superstructure.composite;

import java.util.function.DoubleSupplier;

import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.forgemini.io.Signal;
import com.stzteam.forgemini.io.Tunable;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.models.multimodules.CompositeSubsystem;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.KeyManager;
import frc.robot.configuration.constants.Constants;
import frc.robot.configuration.constants.ModuleConstants.TurretConstants;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOKraken.ArmMODE;
import frc.robot.core.modules.superstructure.modules.climbermodule.Climber;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.requests.moduleRequests.ArmRequest;
import frc.robot.core.requests.moduleRequests.ArmRequestFactory;
import frc.robot.core.requests.moduleRequests.FlyWheelRequest;
import frc.robot.core.requests.moduleRequests.FlyWheelRequestFactory;
import frc.robot.core.requests.moduleRequests.IndexerRequestFactory;
import frc.robot.core.requests.moduleRequests.IntakeRequestFactory;
import frc.robot.core.requests.moduleRequests.TurretRequestFactory;


public class Superstructure extends CompositeSubsystem<SuperstructureData, SuperstructureIO> {

    private static final double NOMINAL_FUEL_VELOCITY_MPS = 18.0;

    @Tunable
    public double RPMTest = -3500;

    @Tunable
    public double AngleTest = -20;

    public Superstructure(SubsystemBuilder<SuperstructureData, SuperstructureIO> builder) {
        super(builder);
    }

    public Climber getClimber() {
        return getSubsystem(KeyManager.CLIMBER_KEY);
    }

    public Turret getTurret() {
        return getSubsystem(KeyManager.TURRET_KEY);
    }

    public Arm getArm() {
        return getSubsystem(KeyManager.ARM_KEY);
    }

    public Intake getIntake() {
        return getSubsystem(KeyManager.INTAKE_KEY);
    }

    public Indexer getIndexer() {
        return getSubsystem(KeyManager.INDEX_KEY);
    }

    public FlyWheel getFlyWheelsIntake() {
        return getSubsystem(KeyManager.FLYWHEEL_INTAKE_KEY);
    }

    public FlyWheel getFlywheelShooter() {
        return getSubsystem(KeyManager.FLYWHEEL_OUTAKE_KEY);
    }

    public Translation2d getVirtualTarget() {
        Turret turret = getTurret();

        Pose2d robotPose = turret.getRobotPose();
        ChassisSpeeds speeds = turret.getRobotSpeeds();

        Pose2d turretPose = robotPose.transformBy(TurretConstants.ROBOT_TO_TURRET_TRANSFORM);

        Translation2d robotVelVector = new Translation2d(
                speeds.vxMetersPerSecond,
                speeds.vyMetersPerSecond).rotateBy(robotPose.getRotation());

        double distanceReal = turretPose.getTranslation().getDistance(Constants.HUB_LOCATION.toTranslation2d());

        double timeOfFlight = distanceReal / NOMINAL_FUEL_VELOCITY_MPS;

        return Constants.HUB_LOCATION.toTranslation2d().minus(robotVelVector.times(timeOfFlight));
    }

    public double getVirtualDistance() {
        Turret turret = getSubsystem(KeyManager.TURRET_KEY);

        Pose2d turretPose = turret.getRobotPose().transformBy(TurretConstants.ROBOT_TO_TURRET_TRANSFORM);

        return turretPose.getTranslation().getDistance(getVirtualTarget());
    }

    public Command lockToHub() {
        Turret turret = getTurret();

        return Commands.parallel(
                turret.setControl(() -> TurretRequestFactory.lockOnTarget()
                .withTarget(()-> this.getVirtualTarget())
                .withTolerance(Constants.TURRET_TOLERANCE)
                .withChassisOmega(() -> turret.getRobotSpeeds().omegaRadiansPerSecond)));
    }

    public Command shoot() {
        FlyWheel flywheelShooter = getFlywheelShooter();
        Indexer index = getIndexer();

        return Commands.sequence(
                Commands.parallel(
                        flywheelShooter
                                .runRequest(() -> FlyWheelRequestFactory.setRPM().toRPM(-3000).withTolerance(Constants.FLYWHEEL_TOLERANCE)))
                        .until(() -> flywheelShooter.isAtTarget(Constants.FLYWHEEL_TOLERANCE)),

                index.setControl(() -> IndexerRequestFactory.moveVoltage().withIndex(8)));
    }

    public Command eatCommand() {
        FlyWheel intakeWheels = getFlyWheelsIntake();
        Indexer index = getIndexer();

        return Commands.parallel(
            intakeWheels.setControl(()-> FlyWheelRequestFactory.moveVoltage().withVolts(-8)),

            index.setControl(()->IndexerRequestFactory.moveVoltage().withIndex(0).withRollers(8))

        );
    }

    public Command clearFuel(){
        FlyWheel intakeFlyWheel = getFlyWheelsIntake();
        Indexer index = getIndexer();

        return Commands.parallel(
            intakeFlyWheel.setControl(()-> FlyWheelRequestFactory.moveVoltage().withVolts(5)),
            index.setControl(()-> IndexerRequestFactory.moveVoltage().withRollers(-12).withIndex(-12)).withTimeout(1.5));
    }


    public Command ShootAngleTest(DoubleSupplier armAngle, DoubleSupplier rpm) {
        Turret turret = getTurret();
        FlyWheel flywheelShooter = getFlywheelShooter();
        Indexer index = getIndexer();
        Arm arm = getArm();
        FlyWheel intakeWheels = getFlyWheelsIntake();

        return Commands.sequence(
                Commands.parallel(
                    turret.setControl(() -> TurretRequestFactory.lockOnTarget()
                                .withTarget(() -> this.getVirtualTarget())
                                .withTolerance(Constants.TURRET_TOLERANCE)
                                .withChassisOmega(() -> turret.getRobotSpeeds().omegaRadiansPerSecond)
                                
                    ),

                    arm.setControl(() -> ArmRequestFactory.setAngle().withAngle(armAngle.getAsDouble())
                                .withTolerance(Constants.ARM_TOLERANCE).withMode(ArmMODE.kUP)),

                    flywheelShooter.runRequest(
                        () -> FlyWheelRequestFactory.setRPM().toRPM(rpm.getAsDouble()).withTolerance(Constants.FLYWHEEL_TOLERANCE)))
                        .until(() -> flywheelShooter.isAtTarget(Constants.FLYWHEEL_TOLERANCE)),
                
                 Commands.parallel(
                index.setControl(() -> IndexerRequestFactory.moveVoltage().withRollers(12).withIndex(12)),
                intakeWheels.setControl(()-> FlyWheelRequestFactory.moveVoltage().withVolts(-8)))
                );

    }
    
    public Command shootOnTheMove(Translation2d turretTarget, ArmRequest armRequest, FlyWheelRequest shooterRequest, double voltIndex) {
        Turret turret = getSubsystem(KeyManager.TURRET_KEY);
        Arm arm = getSubsystem(KeyManager.ARM_KEY);
        FlyWheel flywheelShooter = getSubsystem(KeyManager.FLYWHEEL_OUTAKE_KEY);
        Indexer index = getSubsystem(KeyManager.INDEX_KEY);
        FlyWheel intakeWheels = getFlyWheelsIntake();

        return Commands.sequence(
            Commands.parallel(
                turret.setControl(() -> TurretRequestFactory.lockOnTarget()
                .withTarget(()-> turretTarget)
                .withTolerance(Constants.TURRET_TOLERANCE)
                .withChassisOmega(() -> turret.getRobotSpeeds().omegaRadiansPerSecond)),
                
                arm.setControl(() -> ArmRequestFactory.interpolateTarget()
                .withDistance(() -> this.getVirtualDistance())
                .withTolerance(Constants.ARM_TOLERANCE)),
                
                flywheelShooter.runRequest(() -> FlyWheelRequestFactory.interpolateRPM()
                .withDistance(() -> this.getVirtualDistance())
                .withTolerance(Constants.FLYWHEEL_TOLERANCE))

            ).until(()-> flywheelShooter.isAtTarget(50)),

            Commands.parallel(
                index.setControl(() -> IndexerRequestFactory.moveVoltage().withRollers(voltIndex).withIndex(voltIndex)),
                intakeWheels.setControl(()-> FlyWheelRequestFactory.moveVoltage().withVolts(-8))));
    }

    public Command EatAutoAngle(double angle, double tolerance, intakeMODE mode, double voltage) {

        Intake intake = getIntake();
        FlyWheel intakeWheels = getFlyWheelsIntake();

        return Commands.sequence(
                intake.setControl(
                        () -> IntakeRequestFactory.setAngle().withAngle(angle).Tolerance(tolerance).withMode(mode))
                        .until(() -> intake.isAtTarget(tolerance)),
                intakeWheels.setControl(() -> FlyWheelRequestFactory.moveVoltage().withVolts(voltage)));

    }

    @Signal(key = "RPM")
    public double getRPM() {
        return RPMTest;
    }

    @Signal(key = "Angle")
    public double getAngle() {
        return AngleTest;
    }


    public Command EatAutoWheels(double voltage) {
        FlyWheel intakeWheels = getFlyWheelsIntake();

        return intakeWheels.setControl(() -> FlyWheelRequestFactory.moveVoltage().withVolts(voltage));
    }

    public Command stopAll() {
        Turret turret = getTurret();
        Arm arm = getArm();
        FlyWheel flywheel = getFlyWheelsIntake();
        FlyWheel flywheelout = getFlywheelShooter();
        Indexer index = getIndexer();
        Intake intake = getIntake();

        return Commands.parallel(
                turret.setControl(() -> TurretRequestFactory.idle()),
                arm.setControl(() -> ArmRequestFactory.idle()),
                flywheel.runRequest(() -> FlyWheelRequestFactory.idleIntake()),
                flywheelout.runRequest(() -> FlyWheelRequestFactory.idleOutake()),
                index.setControl(() -> IndexerRequestFactory.idle()),
                intake.setControl(() -> IntakeRequestFactory.idle())
        );

    }

    @Override
    public SuperstructureData getState() {
        return inputs;
    }

    @Override
    public void absolutePeriodic(SuperstructureData inputs) {
        NetworkIO.set(KeyManager.SUPERSTRUCTURE_KEY, "DistanceToHub", getVirtualDistance());
        NetworkIO.set(KeyManager.SUPERSTRUCTURE_KEY, "VirtualTarget", getVirtualTarget());
    }
}