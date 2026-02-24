package frc.robot.configuration;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.pathplanner.lib.commands.PathPlannerAuto;
import com.stzteam.forgemini.io.SmartChooser;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.configuration.advantageScope.visuals.nodes.GamePieceNode;
import frc.robot.configuration.advantageScope.visuals.nodes.GamePieceNode.GamePieceMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.TrajectoryNode;
import frc.robot.configuration.advantageScope.visuals.nodes.VisualizerNode;
import frc.robot.configuration.advantageScope.visuals.nodes.TrajectoryNode.TrajectoryMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.VisualizerNode.VisualizerMsg;
import frc.robot.configuration.constants.ModuleConstants.SwerveConstants;
import frc.robot.configuration.constants.ModuleConstants.TunerConstants;
import frc.robot.configuration.constants.ModuleConstants.VisionConstants;
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
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIOSim;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIOFallback;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIOSim;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.modules.swerve.SwerveTelemetry;
import frc.robot.core.modules.swerve.nodes.LimelightNode;
import frc.robot.core.modules.swerve.nodes.QuestNavNode;
import frc.robot.core.modules.swerve.nodes.VisionNode.VisionMsg;
import mars.source.builder.Builder;
import mars.source.builder.Environment;
import mars.source.builder.Injector;
import mars.source.builder.RunMode;
import mars.source.models.SubsystemBuilder;
import mars.source.operator.ControllerOI;
import mars.source.operator.PS5OI;
import mars.source.operator.XboxOI;

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

    public static final boolean HAS_VISUALS = true;
    public static final boolean HAS_TRAJ_VISUAL = true;
    public static final boolean HAS_FUEL_VISUAL = true;
    public static final boolean HAS_DRIVETRAIN = true;
    public static final boolean HAS_TURRET = true;
    public static final boolean HAS_ARM = true;
    public static final boolean HAS_LIMELIGHT = true;
    public static final boolean HAS_INDEXER = true;
    public static final boolean HAS_QUESTNAV = false;
    public static final boolean HAS_FLYWHEEL = true;
    public static final boolean HAS_INTAKE = true;
    
    public static class SuperstructureBuilder {
        public static Superstructure buildModule(
                Turret turret, 
                Arm arm, 
                Intake intake, 
                Indexer indexer, 
                FlyWheel flywheel) {
            
            SuperstructureIO io = new SuperstructureIO(turret, arm, intake, indexer, flywheel);

            return new Superstructure(SubsystemBuilder.<SuperstructureData, SuperstructureIO>setup()
                .key(KeyManager.SUPERSTRUCTURE_KEY)
                .hardware(io, new SuperstructureData())

            );
        }
    }

    public static class VisualizerBuilder {

        public static VisualizerNode buildNode(
                DoubleSupplier turretAngleSupplier, 
                DoubleSupplier hoodAngleSupplier,
                DoubleSupplier intakeAngleSupplier, 
                Consumer<VisualizerMsg> topicPublisher) {
            
            if(!HAS_VISUALS) return null;

            return new VisualizerNode(
                KeyManager.VISUALIZER_KEY + KeyManager.COMPONENTS_KEY, 
                turretAngleSupplier, 
                hoodAngleSupplier, 
                intakeAngleSupplier,
                topicPublisher
            );
        }
    }

    public static class GamePieceBuilder {
        public static GamePieceNode buildNode(
                Supplier<Pose3d[]> trajectorySource,
                BooleanSupplier trigger,
                Consumer<GamePieceMsg> publisher) {

            if(!HAS_FUEL_VISUAL) return null;
            
            return new GamePieceNode(
                KeyManager.VISUALIZER_KEY + KeyManager.GAMEPIECE_KEY, 
                trajectorySource, 
                trigger,
                publisher
            );
        }
    }

    public static class TrajectoryBuilder {
        public static TrajectoryNode buildNode(
                Supplier<Pose2d> poseSupplier,
                DoubleSupplier turretSupplier,
                DoubleSupplier hoodSupplier,
                Consumer<TrajectoryMsg> publisher) {
            
            if(!HAS_TRAJ_VISUAL) return null;

            return new TrajectoryNode(
                KeyManager.VISUALIZER_KEY + KeyManager.TRAJECTORY_KEY, 
                poseSupplier, 
                turretSupplier, 
                hoodSupplier, 
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

    public static class AutoBuilder {

        public static SmartChooser<Command> build(String chooser){
            return new SmartChooser<>(chooser);
        }
        
        public static Command buildPath(
                String pathName, 
                CommandSwerveDrivetrain drivetrain, 
                QuestNavNode questnav) {
            
            return new SequentialCommandGroup(
                new PathPlannerAuto(pathName),
                
                Commands.runOnce(() -> {
                    if (questnav != null) {
                        questnav.resetQuestPose(new Pose3d(drivetrain.getState().Pose));
                    }
                })
            );
        }

        public static Command buildPath(
                String pathName, 
                CommandSwerveDrivetrain drivetrain) {
            
            return new PathPlannerAuto(pathName);
        }
    }


    public static class DrivetrainBuilder {
        
        public static CommandSwerveDrivetrain buildModule() {
            if (!HAS_DRIVETRAIN) return null;

            CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

            SwerveTelemetry telemetry = new SwerveTelemetry(SwerveConstants.MaxSpeed);
            drivetrain.registerTelemetry(telemetry::telemeterize);

            return drivetrain;
        }
    }

    public static class FlywheelBuilder implements Builder<FlyWheel>{
        
        private FlywheelBuilder(){}

        public static FlywheelBuilder create() { return new FlywheelBuilder();}

        @Override
        public FlyWheel buildModule(){
            FlyWheelIO io = Injector.createIO(
                HAS_FLYWHEEL,
                FlyWheelIOFallback::new,
                FlyWheelIOFallback::new,
                FlyWheelIOSim::new);

            return new FlyWheel(io);
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
            if (HAS_TURRET && this.drivetrain == null) {
                throw new IllegalStateException("Falta el Drivetrain en la Torreta. Usa .withDrivetrain()");
            }

            TurretIO io = Injector.createIO(
                HAS_TURRET, 
                TurretIOFallback::new, 
                TurretIOSim::new,
                TurretIOSim::new
            );

            Supplier<Pose2d> poseSupplier = (this.drivetrain != null) ? () -> this.drivetrain.getState().Pose : () -> Pose2d.kZero;
            Supplier<ChassisSpeeds> speedsSupplier = (this.drivetrain != null) ? this.drivetrain::getChassisSpeeds : () -> new ChassisSpeeds();

            return new Turret(io, poseSupplier, speedsSupplier);
        }
    }

    
    public static class VisionBuilder{
        
        public static LimelightNode limelightNode(
                Supplier<Rotation2d> yawSupplier, 
                DoubleSupplier yawRateSupplier, 
                Consumer<VisionMsg> topicPublisher) {
            
            if (!HAS_LIMELIGHT) return null;

            return new LimelightNode(KeyManager.LIMELIGHT_KEY, yawSupplier, yawRateSupplier, topicPublisher);
        }

        public static QuestNavNode questNode(Consumer<VisionMsg> topicPublisher){

            if(!HAS_QUESTNAV) return null;

            return new QuestNavNode(KeyManager.QUESTNAV_KEY, VisionConstants.ROBOT_TO_QUEST, topicPublisher);
        }

    }

}
