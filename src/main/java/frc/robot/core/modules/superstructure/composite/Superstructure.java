package frc.robot.core.modules.superstructure.composite;

import com.stzteam.forgemini.io.NetworkIO;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.KeyManager;
import frc.robot.configuration.constants.Constants;
import frc.robot.configuration.factories.ArmRequestFactory;
import frc.robot.configuration.factories.FlyWheelsRequestFactory;
import frc.robot.configuration.factories.IndexerRequestFactory;
import frc.robot.configuration.factories.IntakeRequestFactory;
import frc.robot.configuration.factories.TurretRequestFactory;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import mars.source.models.SubsystemBuilder;
import mars.source.models.multimodules.CompositeSubsystem;

public class Superstructure extends CompositeSubsystem<SuperstructureData, SuperstructureIO> {

    private static final double NOMINAL_FUEL_VELOCITY_MPS = 18.0; 

    public Superstructure(SubsystemBuilder<SuperstructureData, SuperstructureIO> builder) {
        super(builder);
    }

    private Translation2d getVirtualTarget() {
        Turret turret = getSubsystem(KeyManager.TURRET_KEY); 
        
        Pose2d robotPose = turret.getRobotPose(); 
        ChassisSpeeds speeds = turret.getRobotSpeeds();

        Translation2d robotVelVector = new Translation2d(
            speeds.vxMetersPerSecond,
            speeds.vyMetersPerSecond
        ).rotateBy(robotPose.getRotation()); 

        double distanceReal = robotPose.getTranslation().getDistance(Constants.HUB_LOCATION.toTranslation2d());
        
        double timeOfFlight = distanceReal / NOMINAL_FUEL_VELOCITY_MPS; 
        
        return Constants.HUB_LOCATION.toTranslation2d().minus(robotVelVector.times(timeOfFlight));
    }

    private double getVirtualDistance() {
        Turret turret = getSubsystem(KeyManager.TURRET_KEY);
        
        Translation2d currentPose = turret.getRobotPose().getTranslation();

        return currentPose.getDistance(getVirtualTarget());
    }

    public Command shootOnTheMove() {
        Turret turret = getSubsystem(KeyManager.TURRET_KEY);
        Arm arm = getSubsystem(KeyManager.ARM_KEY); 
        FlyWheel flywheel = getSubsystem(KeyManager.FLYWHEEL_KEY);
        Indexer index = getSubsystem(KeyManager.INDEX_KEY);

        return Commands.sequence(
            Commands.parallel(
                turret.setControl(() -> TurretRequestFactory.targetLock
                    .withTarget(()-> this.getVirtualTarget())
                    .withTolerance(1.5)
                ),
                
                arm.setControl(() -> ArmRequestFactory.interpolate
                    .withDistance(() -> this.getVirtualDistance())
                    .withTolerance(1.5)
                ),
                
                flywheel.runRequest(() -> FlyWheelsRequestFactory.interpolateRPM.withDistance(() -> this.getVirtualDistance())
                    .withTolerance(50.0)
                )
            ).until(() -> inputs.readyToShoot),

            index.setControl(() -> IndexerRequestFactory.voltage.withVolts(12.0))
                 .withTimeout(0.5)
        );
    }
    
    public Command stopAll() {
        Turret turret = getSubsystem(KeyManager.TURRET_KEY);
        Arm arm = getSubsystem(KeyManager.ARM_KEY); 
        FlyWheel flywheel = getSubsystem(KeyManager.FLYWHEEL_KEY);
        Indexer index = getSubsystem(KeyManager.INDEX_KEY);
        Intake intake = getSubsystem(KeyManager.INTAKE_KEY);

        return Commands.parallel(
            turret.setControl(() -> TurretRequestFactory.idle),
            arm.setControl(() -> ArmRequestFactory.idle),
            flywheel.runRequest(() -> FlyWheelsRequestFactory.Idle),
            index.setControl(() -> IndexerRequestFactory.idle),
            intake.setControl(() -> IntakeRequestFactory.idle)
        );

    }

    @Override
    public SuperstructureData getState() {
        return inputs;
    }

    @Override
    public void absolutePeriodic(SuperstructureData inputs) {
        NetworkIO.set("Superstructure", "DistanciaAlHub", getVirtualDistance());
    }
}