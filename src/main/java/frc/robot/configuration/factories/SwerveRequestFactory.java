package frc.robot.configuration.factories;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import com.ctre.phoenix6.swerve.SwerveModule;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;

import frc.robot.configuration.constants.ModuleConstants.TunerConstants;

import com.ctre.phoenix6.swerve.SwerveRequest;

public class SwerveRequestFactory {

    public static final double MaxSpeed = 1.0 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    public static final double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity
    
    public static final SwerveRequest.Idle idle = new SwerveRequest.Idle();

    public static final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();

    public static final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    public static final SwerveRequest.FieldCentric driveFieldCentric = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors

    public static final SwerveRequest.FieldCentric simpleDriveRequest = new SwerveRequest.FieldCentric();

    public static final SwerveRequest.RobotCentric driveRobotCentric = 
        new SwerveRequest.RobotCentric()
        .withDriveRequestType(SwerveModule.DriveRequestType.OpenLoopVoltage
    );

    public static final SwerveRequest.FieldCentricFacingAngle aimRequest = 
        new SwerveRequest.FieldCentricFacingAngle()
        .withDriveRequestType(SwerveModule.DriveRequestType.OpenLoopVoltage);

    // Objeto reutilizable para aplicar velocidades desde PathPlanner
    public static final SwerveRequest.ApplyRobotSpeeds pathPlannerRequest = new SwerveRequest.ApplyRobotSpeeds();

    /* Swerve requests to apply during SysId characterization */
    public static final SwerveRequest.SysIdSwerveTranslation m_translationCharacterization = new SwerveRequest.SysIdSwerveTranslation();
    public static final SwerveRequest.SysIdSwerveSteerGains m_steerCharacterization = new SwerveRequest.SysIdSwerveSteerGains();
    public static final SwerveRequest.SysIdSwerveRotation m_rotationCharacterization = new SwerveRequest.SysIdSwerveRotation();


}
