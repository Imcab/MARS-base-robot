package frc.robot.helpers;

import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;

/**
 * PoseFinder optimizado para CTRE Phoenix 6 Swerve.
 * Requiere que CommandSwerveDrivetrain haya llamado a configurePathPlanner().
 */
public class PoseFinder{

    private final CommandSwerveDrivetrain drivetrain;
    private final PathConstraints defaultConstraints;
    
    private final SwerveRequest.Idle stopRequest = new SwerveRequest.Idle();

    private Runnable onStart = () -> {};
    private Runnable onFinish = () -> {};

    public PoseFinder(CommandSwerveDrivetrain drivetrain, PathConstraints defaultConstraints) {
        this.drivetrain = drivetrain;
        this.defaultConstraints = defaultConstraints;
    }

    // --- Hooks ---
    public void setOnStart(Runnable hook) { this.onStart = (hook != null) ? hook : () -> {}; }
    public void setOnFinish(Runnable hook) { this.onFinish = (hook != null) ? hook : () -> {}; }

    // --- Helpers de Estado ---
    
    public Pose2d getChassisPose() {
        return drivetrain.getState().Pose;
    }

    public boolean isAtPose(Pose2d target, double toleranceMeters) {
        return getChassisPose().getTranslation().getDistance(target.getTranslation()) < toleranceMeters;
    }

    /**
     * Genera un comando de Pathfinding hacia una Pose.
     * PathPlanner maneja automáticamente el flip de alianza.
     */
    public Command toPose(Pose2d targetPose, PathConstraints constraints) {
        // Usamos defer para calcular la ruta CUANDO se ejecuta el comando, no antes.
        return Commands.defer(() -> {
            return AutoBuilder.pathfindToPose(targetPose, constraints)
                .beforeStarting(() -> {
                    onStart.run();
                })
                .finallyDo(() -> {
                    drivetrain.setControl(stopRequest);
                    onFinish.run();
                })
                .withName("PoseFinder -> " + targetPose.toString());
        }, Set.of(drivetrain));
    }

    public Command toPose(Pose2d targetPose) {
        return toPose(targetPose, defaultConstraints);
    }

    /**
     * Genera un comando hacia un archivo de PathPlanner.
     */
    public Command toPath(String pathName) {
        return Commands.defer(() -> {
            try {
                PathPlannerPath path = PathPlannerPath.fromPathFile(pathName);
                return AutoBuilder.pathfindThenFollowPath(path, defaultConstraints)
                    .beforeStarting(onStart)
                    .finallyDo(() -> {
                        drivetrain.setControl(stopRequest);
                        onFinish.run();
                    });
            } catch (Exception e) {
                DriverStation.reportError("PoseFinder: No se encontró path " + pathName, true);
                return Commands.none();
            }
        }, Set.of(drivetrain));
    }

    /**
     * Busca la pose más cercana de una lista y va hacia ella.
     * Cálculo Lazy: Se decide cuál es la más cercana al momento de presionar el botón.
     */
    public Command toNearestPose(List<Pose2d> poses, PathConstraints constraints) {
        return Commands.defer(() -> {
            Pose2d current = getChassisPose();
            Pose2d nearest = current.nearest(poses);
            return toPose(nearest, constraints);
        }, Set.of(drivetrain));
    }

    public Command toNearestPose(List<Pose2d> poses) {
        return toNearestPose(poses, defaultConstraints);
    }

    public Command toConditionalPose(Pose2d poseTrue, Pose2d poseFalse, BooleanSupplier condition) {
        return Commands.defer(() -> {
            if (condition.getAsBoolean()) {
                return toPose(poseTrue);
            } else {
                return toPose(poseFalse);
            }
        }, Set.of(drivetrain));
    }
}