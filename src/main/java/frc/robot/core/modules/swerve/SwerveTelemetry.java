// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.swerve;

import com.ctre.phoenix6.SignalLogger;
import com.ctre.phoenix6.swerve.SwerveDrivetrain.SwerveDriveState;
import com.stzteam.features.dictionary.Dictionary.CommonTables;
import com.stzteam.forgemini.io.NetworkIO;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.configuration.KeyManager;

public class SwerveTelemetry {

  /**
   * Construct a telemetry object, with the specified max speed of the robot
   *
   * @param maxSpeed Maximum speed in meters per second
   */
  public SwerveTelemetry() {
    SignalLogger.start();
    SignalLogger.setPath("/media/sda1/");
  }

  /* Robot swerve drive state */
  private final String tableName = KeyManager.SWERVE_KEY;

  /* Robot pose for field positioning */
  private final String positioningKey = "Pose";

  private final double[] m_poseArray = new double[3];

  /** Accept the swerve drive state and telemeterize it to SmartDashboard and SignalLogger. */
  public void telemeterize(SwerveDriveState state) {

    /* Telemeterize the swerve drive state */
    NetworkIO.set(tableName, CommonTables.POSE_KEY, state.Pose);
    NetworkIO.set(tableName, CommonTables.GYRO_KEY, state.Pose.getRotation());
    NetworkIO.set(tableName, CommonTables.sPluralOf(CommonTables.SPEED_KEY), state.Speeds);
    NetworkIO.set(
        tableName,
        CommonTables.sPluralOf(CommonTables.MODULE_KEY + CommonTables.STATE_KEY),
        state.ModuleStates);
    NetworkIO.set(
        tableName,
        CommonTables.sPluralOf(CommonTables.MODULE_KEY + CommonTables.TARGET_KEY),
        state.ModuleTargets);
    NetworkIO.set(
        tableName,
        CommonTables.sPluralOf(CommonTables.MODULE_KEY + CommonTables.POSITION_KEY),
        state.ModulePositions);
    NetworkIO.set(tableName, CommonTables.TIMESTAMP_KEY, state.Timestamp);
    NetworkIO.set(
        tableName,
        CommonTables.ODOMETRY_KEY + CommonTables.FREQUENCY_KEY,
        1.0 / state.OdometryPeriod);

    /* Also write to log file */
    SignalLogger.writeStruct("DriveState/Pose", Pose2d.struct, state.Pose);
    SignalLogger.writeStruct("DriveState/Speeds", ChassisSpeeds.struct, state.Speeds);
    SignalLogger.writeStructArray(
        "DriveState/ModuleStates", SwerveModuleState.struct, state.ModuleStates);
    SignalLogger.writeStructArray(
        "DriveState/ModuleTargets", SwerveModuleState.struct, state.ModuleTargets);
    SignalLogger.writeStructArray(
        "DriveState/ModulePositions", SwerveModulePosition.struct, state.ModulePositions);
    SignalLogger.writeDouble("DriveState/OdometryPeriod", state.OdometryPeriod, "seconds");

    /* Telemeterize the pose to a Field2d */
    NetworkIO.set(positioningKey, ".type", "Field2d");

    m_poseArray[0] = state.Pose.getX();
    m_poseArray[1] = state.Pose.getY();
    m_poseArray[2] = state.Pose.getRotation().getDegrees();

    NetworkIO.set(positioningKey, CommonTables.ROBOT_KEY + CommonTables.POSE_KEY, m_poseArray);
  }
}
