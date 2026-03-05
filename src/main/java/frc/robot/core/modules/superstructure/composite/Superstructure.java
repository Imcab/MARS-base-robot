package frc.robot.core.modules.superstructure.composite;

import java.security.PublicKey;
import java.util.function.DoubleSupplier;

import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.forgemini.io.Signal;
import com.stzteam.forgemini.io.Tunable;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.models.multimodules.CompositeSubsystem;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.KeyManager;
import frc.robot.configuration.constants.Constants;

import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOKraken.ArmMODE;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.requests.moduleRequests.ArmRequestFactory;
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

    private Translation2d getVirtualTarget() {
        Turret turret = getSubsystem(KeyManager.TURRET_KEY);

        Pose2d robotPose = turret.getRobotPose();
        ChassisSpeeds speeds = turret.getRobotSpeeds();

        Translation2d robotVelVector = new Translation2d(
                speeds.vxMetersPerSecond,
                speeds.vyMetersPerSecond).rotateBy(robotPose.getRotation());

        double distanceReal = robotPose.getTranslation().getDistance(Constants.HUB_LOCATION.toTranslation2d());

        double timeOfFlight = distanceReal / NOMINAL_FUEL_VELOCITY_MPS;

        return Constants.HUB_LOCATION.toTranslation2d().minus(robotVelVector.times(timeOfFlight));
    }

    private double getVirtualDistance() {
        Turret turret = getSubsystem(KeyManager.TURRET_KEY);

        Translation2d currentPose = turret.getRobotPose().getTranslation();

        return currentPose.getDistance(getVirtualTarget());
    }

    public Command lockToHub() {
        Turret turret = getSubsystem(KeyManager.TURRET_KEY);

        return Commands.parallel(
                turret.setControl(() -> TurretRequestFactory.lockOnTarget()
                        .withTarget(() -> this.getVirtualTarget())));
    }

    public Command shoot() {
        FlyWheel flywheelShooter = getSubsystem(KeyManager.FLYWHEEL_OUTAKE_KEY);
        Indexer index = getSubsystem(KeyManager.INDEX_KEY);

        return Commands.sequence(
                Commands.parallel(
                        flywheelShooter
                                .runRequest(() -> FlyWheelRequestFactory.setRPM().toRPM(-3000).withTolerance(50)))
                        .until(() -> flywheelShooter.isAtTarget(50)),

                index.setControl(() -> IndexerRequestFactory.moveVoltage().withIndex(8)));
    }

    public Command eatCommand() {
        FlyWheel intakeWheels = getSubsystem(KeyManager.FLYWHEEL_INTAKE_KEY);
        Indexer index = getSubsystem(KeyManager.INDEX_KEY);

        return Commands.parallel(
                intakeWheels.setControl(() -> FlyWheelRequestFactory.moveVoltage().withVolts(-9)),

                index.setControl(() -> IndexerRequestFactory.moveVoltage().withRollers(8))

        );
    }

    public Command ShootAngle(double armAngle, double rpm) {
        Turret turret = getSubsystem(KeyManager.TURRET_KEY);
        FlyWheel flywheelShooter = getSubsystem(KeyManager.FLYWHEEL_OUTAKE_KEY);
        Indexer index = getSubsystem(KeyManager.INDEX_KEY);
        Arm arm = getSubsystem(KeyManager.ARM_KEY);

        return Commands.sequence(
                Commands.parallel(
                        turret.setControl(() -> TurretRequestFactory.lockOnTarget()
                                .withTarget(() -> this.getVirtualTarget())
                                .withTolerance(Constants.TURRET_TOLERANCE)),

                        arm.setControl(() -> ArmRequestFactory.setAngle().withAngle(armAngle).withTolerance(2)
                                .withMode(ArmMODE.kUP)),

                        flywheelShooter.runRequest(() -> FlyWheelRequestFactory.setRPM().toRPM(rpm).withTolerance(50)))
                        .until(() -> flywheelShooter.isAtTarget(50)),

                index.setControl(() -> IndexerRequestFactory.moveVoltage().withIndex(12).withRollers(12)));

    }

    public Command ShootAngleTest(DoubleSupplier armAngle, DoubleSupplier rpm) {
        Turret turret = getSubsystem(KeyManager.TURRET_KEY);
        FlyWheel flywheelShooter = getSubsystem(KeyManager.FLYWHEEL_OUTAKE_KEY);
        Indexer index = getSubsystem(KeyManager.INDEX_KEY);
        Arm arm = getSubsystem(KeyManager.ARM_KEY);

        return Commands.sequence(
                Commands.parallel(
                        turret.setControl(() -> TurretRequestFactory.lockOnTarget()
                                .withTarget(() -> this.getVirtualTarget())
                                .withTolerance(Constants.TURRET_TOLERANCE)),

                        arm.setControl(() -> ArmRequestFactory.setAngle().withAngle(armAngle.getAsDouble())
                                .withTolerance(2).withMode(ArmMODE.kUP)),

                        flywheelShooter.runRequest(
                                () -> FlyWheelRequestFactory.setRPM().toRPM(rpm.getAsDouble()).withTolerance(50)))
                        .until(() -> flywheelShooter.isAtTarget(50)),

                index.setControl(() -> IndexerRequestFactory.moveVoltage().withIndex(12).withRollers(12)));

    }

    public Command shootOnTheMove() {
        Turret turret = getSubsystem(KeyManager.TURRET_KEY);
        Arm arm = getSubsystem(KeyManager.ARM_KEY);
        FlyWheel flywheelShooter = getSubsystem(KeyManager.FLYWHEEL_OUTAKE_KEY);
        Indexer index = getSubsystem(KeyManager.INDEX_KEY);

        return Commands.sequence(
                Commands.parallel(
                        turret.setControl(() -> TurretRequestFactory.lockOnTarget()
                                .withTarget(() -> this.getVirtualTarget())
                                .withTolerance(Constants.TURRET_TOLERANCE)),

                        arm.setControl(() -> ArmRequestFactory.interpolateTarget()
                                .withDistance(() -> this.getVirtualDistance())
                                .withTolerance(Constants.ARM_TOLERANCE)),

                        flywheelShooter.runRequest(() -> FlyWheelRequestFactory.interpolateRPM()
                                .withDistance(() -> this.getVirtualDistance())
                                .withTolerance(Constants.FLYWHEEL_TOLERANCE)))
                        .until(() -> flywheelShooter.isAtTarget(50)),

                index.setControl(() -> IndexerRequestFactory.moveVoltage().withRollers(8).withIndex(8)));
    }

    public Command EatAutoAngle(double angle, double tolerance, intakeMODE mode, double voltage) {

        Intake intake = getSubsystem(KeyManager.INTAKE_KEY);
        FlyWheel intakeWheels = getSubsystem(KeyManager.FLYWHEEL_INTAKE_KEY);

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
        FlyWheel intakeWheels = getSubsystem(KeyManager.FLYWHEEL_INTAKE_KEY);

        return intakeWheels.setControl(() -> FlyWheelRequestFactory.moveVoltage().withVolts(voltage));
    }

    public Command stopAll() {
        Turret turret = getSubsystem(KeyManager.TURRET_KEY);
        Arm arm = getSubsystem(KeyManager.ARM_KEY);
        FlyWheel flywheel = getSubsystem(KeyManager.FLYWHEEL_INTAKE_KEY);
        FlyWheel flywheelout = getSubsystem(KeyManager.FLYWHEEL_OUTAKE_KEY);
        Indexer index = getSubsystem(KeyManager.INDEX_KEY);
        Intake intake = getSubsystem(KeyManager.INTAKE_KEY);

        return Commands.parallel(
                turret.setControl(() -> TurretRequestFactory.idle()),
                arm.setControl(() -> ArmRequestFactory.idle()),
                flywheel.runRequest(() -> FlyWheelRequestFactory.idle()),
                flywheelout.runRequest(() -> FlyWheelRequestFactory.idle()),
                index.setControl(() -> IndexerRequestFactory.idle()),
                intake.setControl(() -> IntakeRequestFactory.idle()));

    }

    @Override
    public SuperstructureData getState() {
        return inputs;
    }

    @Override
    public void absolutePeriodic(SuperstructureData inputs) {
        NetworkIO.set("Superstructure", "DistanciaAlHub", getVirtualDistance());
        NetworkIO.set("Superstructure", "Virtual", getVirtualTarget());
    }
}