package frc.robot.configuration.advantageScope.visuals.drivers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import frc.robot.configuration.advantageScope.visuals.nodes.TrajectoryNode.TrajectoryIO;
import frc.robot.configuration.advantageScope.visuals.nodes.TrajectoryNode.TrajectoryMsg;

public class TrajectoryDriver implements TrajectoryIO {

    private final Supplier<Pose2d> robotPoseSupplier;
    private final DoubleSupplier turretAngleSupplier;
    private final DoubleSupplier velocitySupplier;

    public static final Translation3d HUB_LOCATION = new Translation3d(4.63, 4.04, 1.90);

    private final double GRAVITY = 9.81;
    private final double TIME_STEP = 0.05; 
    
    private final Translation3d SHOOTER_ORIGIN_OFFSET = new Translation3d(0.15, 0.0, 0.44); 

    public TrajectoryDriver(
        Supplier<Pose2d> robotPose, 
        DoubleSupplier turretAngle, 
        DoubleSupplier velocitySupplier
    ) {
        this.robotPoseSupplier = robotPose;
        this.turretAngleSupplier = turretAngle;
        this.velocitySupplier = velocitySupplier;
    }

    @Override
    public void updateData(TrajectoryMsg data) {

        Pose2d robotPose = robotPoseSupplier.get();
        double turretDeg = turretAngleSupplier.getAsDouble();
        double shotSpeed = velocitySupplier.getAsDouble();

        if (shotSpeed < 1.0) shotSpeed = 10.0;

        Rotation2d combinedYaw = robotPose.getRotation().plus(Rotation2d.fromDegrees(turretDeg));
        
        Translation3d startPoint = new Translation3d(robotPose.getX(), robotPose.getY(), 0.0)
            .plus(SHOOTER_ORIGIN_OFFSET.rotateBy(new Rotation3d(0, 0, combinedYaw.getRadians())));

        double dx = HUB_LOCATION.getX() - startPoint.getX();
        double dy = HUB_LOCATION.getY() - startPoint.getY();
        double dz = HUB_LOCATION.getZ() - startPoint.getZ();
        
        double horizontalDist = Math.sqrt(dx*dx + dy*dy);

        double timeOfFlight = horizontalDist / (shotSpeed * 0.9);

        double vx = dx / timeOfFlight;
        double vy = dy / timeOfFlight;
        
        double vz = (dz + 0.5 * GRAVITY * timeOfFlight * timeOfFlight) / timeOfFlight;

        List<Pose3d> path = new ArrayList<>();
        double currentX = startPoint.getX();
        double currentY = startPoint.getY();
        double currentZ = startPoint.getZ();
        
        for (double t = 0; t <= timeOfFlight; t += TIME_STEP) {

            path.add(new Pose3d(currentX, currentY, currentZ, new Rotation3d()));

            currentX = startPoint.getX() + vx * t;
            currentY = startPoint.getY() + vy * t;
            currentZ = startPoint.getZ() + vz * t - 0.5 * GRAVITY * t * t;
        }

        path.add(new Pose3d(HUB_LOCATION, new Rotation3d()));

        data.trajectory = path.toArray(new Pose3d[0]);
    }

}