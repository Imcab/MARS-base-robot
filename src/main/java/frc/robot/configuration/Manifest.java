package frc.robot.configuration;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIO;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOKraken;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOSim;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIOSim;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.modules.swerve.nodes.LimelightNode;
import frc.robot.core.modules.swerve.nodes.VisionNode.VisionMsg;

public class Manifest {

    public enum Mode { REAL, SIM }
    public static final Mode CURRENT_MODE = Mode.SIM;

    public static final boolean HAS_DRIVETRAIN = true;
    public static final boolean HAS_TURRET = true;
    public static final boolean HAS_ARM = true;
    public static final boolean HAS_LIMELIGHT = true;
 
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

    public static class TurretBuilder {
        private static TurretIO injectIO() {
            switch (CURRENT_MODE) {
                // case REAL: return new TurretIOSparkMax();
                case SIM:
                default:   return new TurretIOSim();
            }
        }

        // Requiere el Drivetrain para calcular la cinemática
        public static Turret buildModule(CommandSwerveDrivetrain drivetrain) {
            if (!HAS_TURRET) return null;
            
            // Asumiendo que tu torreta pide IO, un Supplier de Pose y un Supplier de Velocidad
            return new Turret(
                injectIO(), 
                () -> drivetrain.getState().Pose, 
                drivetrain::getChassisSpeeds
            );
        }
    }

    public static class VisionBuilder {
        
        public static LimelightNode buildLimelightNode(
                Supplier<Rotation2d> yawSupplier, 
                DoubleSupplier yawRateSupplier, 
                Consumer<VisionMsg> topicPublisher) {
            
            if (!HAS_LIMELIGHT) return null;

            return new LimelightNode(KeyManager.LIMELIGHT_KEY, yawSupplier, yawRateSupplier, topicPublisher);
        }
    }

}
