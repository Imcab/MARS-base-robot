package frc.robot.configuration;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.stzteam.mars.builder.Builder;
import com.stzteam.mars.builder.Environment;
import com.stzteam.mars.builder.Environment.RunMode;
import com.stzteam.mars.builder.Injector;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.operator.ControllerOI;
import com.stzteam.mars.operator.PS5OI;
import com.stzteam.mars.operator.XboxOI;
import com.stzteam.mars.services.nodes.FallbackNode;
import com.stzteam.mars.services.nodes.Node;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode.GamePieceMsg;
import frc.robot.configuration.constants.ModuleConstants.TunerConstants;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.modules.superstructure.composite.SuperstructureData;
import frc.robot.core.modules.superstructure.composite.SuperstructureIO;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIO;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOFallback;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOKraken;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOSim;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerIO;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerIOFallback;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerIOSim;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerSparkMax;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIO;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOFallback;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOSim;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIOFallback;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIOKrakenIntake;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIOKrakenShooter;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIOSim;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIOFallback;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIOSim;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIOSparkMax;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.modules.swerve.SwerveTelemetry;

import frc.robot.configuration.advantageScope.visuals.nodes.visualizer.VisualizerNode;
import frc.robot.configuration.advantageScope.visuals.nodes.visualizer.VisualizerNode.VisualizerMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode.TrajectoryMsg;

public class Manifest {

    public enum ControllerType { PS5, XBOX }

    private static final int DRIVER_PORT = 0;
    private static final int OPERATOR_PORT = 1;

    public static final RunMode CURRENT_MODE = RunMode.SIM;

    static{
        Environment.setMode(CURRENT_MODE);
    }

    public static final ControllerType DRIVER_CONTROLLER = ControllerType.XBOX;
    public static final ControllerType OPERATOR_CONTROLLER = ControllerType.XBOX;

    public static final boolean HAS_DRIVETRAIN = true;

    public static final boolean HAS_VISUALS = false;
    public static final boolean HAS_TRAJ_VISUAL = false;
    public static final boolean HAS_FUEL_VISUAL = false;
    public static final boolean HAS_TURRET = true;
    public static final boolean HAS_ARM = true;
    public static final boolean HAS_LIMELIGHT = true; 
    public static final boolean HAS_INDEXER = true;
    public static final boolean HAS_QUESTNAV = false;
    public static final boolean HAS_SHOOTER_WHEELS = true;
    public static final boolean HAS_INTAKE = true;
    public static final boolean HAS_INTAKE_WHEELS = true;
   
    public static class SuperstructureBuilder {
        public static Superstructure superBuild(
                Turret turret, 
                Arm arm, 
                Intake intake, 
                Indexer indexer, 
                FlyWheel flywheelShooter,
                FlyWheel flywheelIntake
                ) {
            
            SuperstructureIO io = new SuperstructureIO(turret, arm, intake, indexer, flywheelShooter, flywheelIntake);

            return new Superstructure(SubsystemBuilder.<SuperstructureData, SuperstructureIO>setup()
                .key(KeyManager.SUPERSTRUCTURE_KEY)
                .hardware(io, new SuperstructureData())

            );
        }
    }

    public static class VisualizerBuilder {
        public static Node<VisualizerMsg> buildNode(
                DoubleSupplier turretAngleSupplier, 
                DoubleSupplier hoodAngleSupplier,
                DoubleSupplier intakeAngleSupplier, 
                Consumer<VisualizerMsg> topicPublisher) {
            
            if(!HAS_VISUALS) {
                return new FallbackNode<>();
            }

            return new VisualizerNode(
                KeyManager.VISUALIZER_KEY + KeyManager.COMPONENTS_KEY, 
                turretAngleSupplier, 
                hoodAngleSupplier, 
                intakeAngleSupplier,
                topicPublisher
            );
        }
    }

    public static class TrajectoryBuilder {
        public static Node<TrajectoryMsg> buildNode(
                Supplier<Pose2d> poseSupplier,
                DoubleSupplier turretSupplier,
                DoubleSupplier velocitySupplier,
                Consumer<TrajectoryMsg> publisher) {
            
            if(!HAS_TRAJ_VISUAL) {
                return new FallbackNode<>();
            }

            return new TrajectoryNode(
                KeyManager.VISUALIZER_KEY + KeyManager.TRAJECTORY_KEY, 
                poseSupplier, 
                turretSupplier, 
                velocitySupplier, 
                publisher
            );
        }
    }


    public static class GamePieceBuilder {
        public static Node<GamePieceMsg> buildNode(Consumer<GamePieceMsg> publisher) {

            if(!HAS_FUEL_VISUAL) return new FallbackNode<>();
            
            return new GamePieceNode(
                KeyManager.VISUALIZER_KEY + KeyManager.GAMEPIECE_KEY, 
                publisher
            );
        }
    }

    public static class ControlsBuilder {
        
        public static ControllerOI buildDriver() {
            return DRIVER_CONTROLLER == ControllerType.PS5 
                   ? new PS5OI(DRIVER_PORT) 
                   : new XboxOI(DRIVER_PORT);
        }
        public static ControllerOI buildOperator() {
            return OPERATOR_CONTROLLER == ControllerType.PS5 
                   ? new PS5OI(OPERATOR_PORT) 
                   : new XboxOI(OPERATOR_PORT);
        }
    }

    public static class DrivetrainBuilder {
        
        public static CommandSwerveDrivetrain buildModule() {
            if (!HAS_DRIVETRAIN) return null;

            CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

            SwerveTelemetry telemetry = new SwerveTelemetry();
            drivetrain.registerTelemetry(telemetry::telemeterize);

            return drivetrain;
        }
    }

    public static class FlywheelShooterBuilder implements Builder<FlyWheel>{
        
        private FlywheelShooterBuilder(){}

        public static FlywheelShooterBuilder create() { return new FlywheelShooterBuilder();}

        @Override
        public FlyWheel buildModule(){
            FlyWheelIO io = Injector.createIO(
                HAS_SHOOTER_WHEELS,
                FlyWheelIOFallback::new,
                FlyWheelIOKrakenShooter::new,
                FlyWheelIOSim::new);

            return new FlyWheel(io, KeyManager.FLYWHEEL_OUTAKE_KEY);
        }
    }

    public static class FlywheelIntakeBuilder implements Builder<FlyWheel>{
        
        private FlywheelIntakeBuilder(){}

        public static FlywheelIntakeBuilder create() {return new FlywheelIntakeBuilder();}

        @Override
        public FlyWheel buildModule(){
            FlyWheelIO io = Injector.createIO(
                HAS_INTAKE_WHEELS,
                FlyWheelIOFallback::new,
                FlyWheelIOKrakenIntake::new,
                FlyWheelIOSim::new);

            return new FlyWheel(io, KeyManager.FLYWHEEL_INTAKE_KEY);
        }
    }

    public static class ArmBuilder implements Builder<Arm> {
        
        private ArmBuilder() {}
        public static ArmBuilder create() { return new ArmBuilder(); }

        @Override
        public Arm buildModule() {
            ArmIO io = Injector.createIO(
                HAS_ARM, 
                ArmIOFallback::new, 
                ArmIOKraken::new, 
                ArmIOSim::new
            );
            return new Arm(io);
        }
    }

    public static class IntakeBuilder implements Builder<Intake> {
        
        private IntakeBuilder() {}
        public static IntakeBuilder create() { return new IntakeBuilder(); }

        @Override
        public Intake buildModule() {
            IntakeIO io = Injector.createIO(
                HAS_INTAKE, 
                IntakeIOFallback::new, 
                IntakeIOKraken::new, 
                IntakeIOSim::new
            );
            return new Intake(io);
        }
    }

    public static class IndexerBuilder implements Builder<Indexer> {
        
        private IndexerBuilder() {}
        public static IndexerBuilder create() { return new IndexerBuilder(); }

        @Override
        public Indexer buildModule() {
            IndexerIO io = Injector.createIO(
                HAS_INDEXER, 
                IndexerIOFallback::new, 
                IndexerSparkMax::new, 
                IndexerIOSim::new
            );
            return new Indexer(io);
        }
    }

    public static class TurretBuilder implements Builder<Turret> {
        
        private CommandSwerveDrivetrain drivetrain;

        private TurretBuilder() {}
        public static TurretBuilder create() { return new TurretBuilder(); }

        public TurretBuilder withDrivetrain(CommandSwerveDrivetrain dt) {
            this.drivetrain = dt;
            return this;
        }

        @Override
        public Turret buildModule() {
            if (HAS_TURRET) {
                if (this.drivetrain == null) {
                    throw new IllegalStateException("Falta el Drivetrain en la Torreta. Usa .withDrivetrain()");
                }
            }

            TurretIO io = Injector.createIO(
                HAS_TURRET, 
                TurretIOFallback::new, 
                TurretIOSparkMax::new,
                TurretIOSim::new
            );

            Supplier<Pose2d> poseSupplier = (this.drivetrain != null) ? () -> this.drivetrain.getState().Pose : () -> Pose2d.kZero;
            Supplier<ChassisSpeeds> speedsSupplier = (this.drivetrain != null) ? this.drivetrain::getChassisSpeeds : () -> new ChassisSpeeds();

            return new Turret(io, poseSupplier, speedsSupplier);
        }
    }

}