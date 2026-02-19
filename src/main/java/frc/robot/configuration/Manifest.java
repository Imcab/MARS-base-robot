package frc.robot.configuration;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.pathplanner.lib.commands.PathPlannerAuto;
import com.stzteam.forgemini.io.SmartChooser;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.configuration.advantageScope.visualsNode.VisualizerNode;
import frc.robot.configuration.advantageScope.visualsNode.VisualizerNode.VisualizerMsg;
import frc.robot.configuration.constants.ModuleConstants.SwerveConstants;
import frc.robot.configuration.constants.ModuleConstants.TunerConstants;
import frc.robot.configuration.constants.ModuleConstants.VisionConstants;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIO;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOKraken;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOSim;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerIO;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerIOSim;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerSparkMax;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIO;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOSim;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIOSim;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIOSim;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.modules.swerve.SwerveTelemetry;
import frc.robot.core.modules.swerve.nodes.LimelightNode;
import frc.robot.core.modules.swerve.nodes.QuestNavNode;
import frc.robot.core.modules.swerve.nodes.VisionNode.VisionMsg;
import mars.source.operator.ControllerOI;
import mars.source.operator.PS5OI;
import mars.source.operator.XboxOI;

public class Manifest {

    public enum Mode { REAL, SIM }
    public enum ControllerType { PS5, XBOX }

    private static final int DRIVER_PORT = 0;
    private static final int OPERATOR_PORT = 1;

    public static final Mode CURRENT_MODE = Mode.SIM;

    public static final ControllerType DRIVER_CONTROLLER = ControllerType.XBOX;
    public static final ControllerType OPERATOR_CONTROLLER = ControllerType.XBOX;

    public static final boolean HAS_VISUALS = true;
    public static final boolean HAS_DRIVETRAIN = true;
    public static final boolean HAS_TURRET = true;
    public static final boolean HAS_ARM = true;
    public static final boolean HAS_LIMELIGHT = true;
    public static final boolean HAS_INDEXER = true;
    public static final boolean HAS_QUESTNAV = false;
    public static final boolean HAS_FLYWHEEL = true;
    public static final boolean HAS_INTAKE = true;
    

    public static class VisualizerBuilder {

        public static VisualizerNode buildNode(
                DoubleSupplier turretAngleSupplier, 
                DoubleSupplier hoodAngleSupplier, 
                Consumer<VisualizerMsg> topicPublisher) {
            
            if(!HAS_VISUALS) return null;

            return new VisualizerNode(
                "Visualizer", 
                turretAngleSupplier, 
                hoodAngleSupplier, 
                topicPublisher
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

    public static class FlywheelBuilder{
        private static FlyWheelIO injectIO(){
            switch (CURRENT_MODE) {
                case REAL:
                    return null;
                case SIM:
                default:
                    return new FlyWheelIOSim();
            }
        }

        public static FlyWheel buildModule(){
            if(!HAS_FLYWHEEL) return null;

            return new FlyWheel(injectIO());
        }
    }

    public static class ArmBuilder{
        
        private static ArmIO injectIO() {
            switch (CURRENT_MODE) {
                case REAL: return new ArmIOKraken();
                case SIM:
                default:   return new ArmIOSim();
            }
        }

        public static Arm buildModule() {
            if (!HAS_ARM) return null;

            return new Arm(injectIO());
        }
    }

    public static class IntakeBuilder{
        
        private static IntakeIO injectIO() {
            switch (CURRENT_MODE) {
                case REAL: return new IntakeIOKraken();
                case SIM:
                default:   return new IntakeIOSim();
            }
        }

        public static Intake buildModule() {
            if (!HAS_ARM) return null;

            return new Intake(injectIO());
        }
    }

    public static class IndexerBuilder{
        
        private static IndexerIO injectIO() {
            switch (CURRENT_MODE) {
                case REAL: return new IndexerSparkMax();
                case SIM:
                default:   return new IndexerIOSim();
            }
        }

        public static Indexer buildModule() {
            if (!HAS_ARM) return null;

            return new Indexer(injectIO());
        }
    }


    public static class TurretBuilder {
        private static TurretIO injectIO() {
            switch (CURRENT_MODE) {
                // case REAL: return new TurretIOSparkMax();
                case SIM:
                default:   return new TurretIOSim();
            }
        }

        public static Turret buildModule(CommandSwerveDrivetrain drivetrain) {
            if (!HAS_TURRET) return null;
            
            return new Turret(
                injectIO(), 
                () -> drivetrain.getState().Pose, 
                drivetrain::getChassisSpeeds
            );
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
