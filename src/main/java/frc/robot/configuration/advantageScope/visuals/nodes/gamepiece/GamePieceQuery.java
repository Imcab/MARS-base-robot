package frc.robot.configuration.advantageScope.visuals.nodes.gamepiece;

import com.stzteam.mars.services.Query;

import edu.wpi.first.math.geometry.Pose3d;

public class GamePieceQuery implements Query {
    public final Pose3d[] trajectory;
    public final boolean fireTrigger;

    public GamePieceQuery(Pose3d[] trajectory, boolean fireTrigger) {
        this.trajectory = trajectory;
        this.fireTrigger = fireTrigger;
    }
}
