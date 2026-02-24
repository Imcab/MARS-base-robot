package frc.robot.configuration.advantageScope.visuals;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode.GamePieceMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceQuery;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode.TrajectoryMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryQuery;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryReply;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import mars.source.services.Service;
import mars.source.services.nodes.Node;

public class VisualsFactory {

    @SuppressWarnings("unchecked")
    public static Command triggerShootVisuals(
            Node<TrajectoryMsg> trajectoryNode,
            Node<GamePieceMsg> gamePieceViz,
            CommandSwerveDrivetrain drivetrain,
            Turret turret) {

        return Commands.runOnce(() -> {

            if (trajectoryNode instanceof Service serviceT && 
                gamePieceViz instanceof Service serviceG) {
                
                TrajectoryReply reply = (TrajectoryReply) serviceT.execute(
                    new TrajectoryQuery(
                        drivetrain.getState().Pose, 
                        turret.getDegrees(), 
                        15.0 // Velocidad de salida
                    )
                );

                serviceG.execute(new GamePieceQuery(reply.path, true));
            }
        });
    }
}