package frc.robot.configuration.advantageScope.visuals.drivers;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose3d;
import frc.robot.configuration.advantageScope.visuals.nodes.GamePieceNode.GamePieceIO;
import frc.robot.configuration.advantageScope.visuals.nodes.GamePieceNode.GamePieceMsg;


public class GamePieceDriver implements GamePieceIO {

    private final Supplier<Pose3d[]> trajectorySource;
    private final BooleanSupplier triggerSupplier;

    private boolean isFlying = false;
    private Pose3d[] activeTrajectory;
    private int animationIndex = 0;

    public GamePieceDriver(Supplier<Pose3d[]> trajectorySource, BooleanSupplier triggerSupplier) {
        this.trajectorySource = trajectorySource;
        this.triggerSupplier = triggerSupplier;
    }

    @Override
    public void updateData(GamePieceMsg data) {
        boolean trigger = triggerSupplier.getAsBoolean();

        if (trigger && !isFlying) {
            this.activeTrajectory = trajectorySource.get();
            if (this.activeTrajectory != null && this.activeTrajectory.length > 0) {
                this.isFlying = true;
                this.animationIndex = 0;
            }
        }

        if (isFlying) {
            if (animationIndex < activeTrajectory.length) {
                data.visualizerPose = new Pose3d[] { activeTrajectory[animationIndex] };
                
                animationIndex++; 
            } else {
                isFlying = false;
                data.visualizerPose = new Pose3d[0];
            }
        } else {

            data.visualizerPose = new Pose3d[0];
        }
    }
}