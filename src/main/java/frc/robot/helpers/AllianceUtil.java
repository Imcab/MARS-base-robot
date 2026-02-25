package frc.robot.helpers;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;

public class AllianceUtil{

    public static final double FIELD_LENGTH_METERS = 16.54175;

    public static Translation2d flip(Translation2d translation) {
        return new Translation2d(FIELD_LENGTH_METERS - translation.getX(), translation.getY());
    }

    public static Rotation2d flip(Rotation2d rotation) {
        return new Rotation2d(Math.PI - rotation.getRadians());
    }

    public static Pose2d flip(Pose2d pose) {
        return new Pose2d(flip(pose.getTranslation()), flip(pose.getRotation()));
    }

    public static Translation3d flip(Translation3d translation) {
        return new Translation3d(FIELD_LENGTH_METERS - translation.getX(), translation.getY(), translation.getZ());
    }

    public static Rotation3d flip(Rotation3d rotation) {
  
        return new Rotation3d(
            -rotation.getX(), 
            rotation.getY(), 
            Math.PI - rotation.getZ()
        );
    }

    public static Pose3d flip(Pose3d pose) {
        return new Pose3d(flip(pose.getTranslation()), flip(pose.getRotation()));
    }
}
