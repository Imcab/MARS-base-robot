package frc.robot.core.modules.swerve;

import java.util.Optional;
import java.util.function.Supplier;

import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.swerve.SwerveModuleConstants;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.RobotConfig;
import com.stzteam.forgemini.io.NetworkIO;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.configuration.KeyManager;
import frc.robot.configuration.constants.SwerveConstants;
import frc.robot.configuration.constants.TunerConstants.TunerSwerveDrivetrain;
import frc.robot.configuration.factory.SwerveRequestFactory;
import frc.robot.helpers.PoseFinder;
import frc.robot.helpers.SysIdRoutineManager;

// IMPORTANTE: Importamos el mensaje de tu Nodo de Visión
import frc.robot.core.modules.swerve.nodes.VisionNode.VisionMsg;

/**
 * Class that extends the Phoenix 6 SwerveDrivetrain class and implements
 * Subsystem so it can easily be used in command-based projects.
 *
 * Arquitectura Limpia: Cero referencias a hardware de visión específico.
 */
public class CommandSwerveDrivetrain extends TunerSwerveDrivetrain implements Subsystem {
     // 4 ms
    private Notifier m_simNotifier = null;
    private double m_lastSimTime;

    private final Field2d field = new Field2d();

    private boolean m_hasAppliedOperatorPerspective = false;

    public SysIdRoutineManager sysIdManager;
    private SysIdRoutine m_sysIdRoutineToApply = null;
    
    private PoseFinder finder;
        
         
    public CommandSwerveDrivetrain(SwerveDrivetrainConstants drivetrainConstants, SwerveModuleConstants<?, ?, ?>... modules) {
                super(drivetrainConstants, modules);
                finishSetup();
    }   
        
    public CommandSwerveDrivetrain(SwerveDrivetrainConstants drivetrainConstants, double odometryUpdateFrequency, SwerveModuleConstants<?, ?, ?>... modules) {
                super(drivetrainConstants, odometryUpdateFrequency, modules);
                finishSetup();
    }
        
    public CommandSwerveDrivetrain(
                SwerveDrivetrainConstants drivetrainConstants,
                double odometryUpdateFrequency,
                Matrix<N3, N1> odometryStandardDeviation,
                Matrix<N3, N1> visionStandardDeviation,
                SwerveModuleConstants<?, ?, ?>... modules
            ) {
                super(drivetrainConstants, odometryUpdateFrequency, odometryStandardDeviation, visionStandardDeviation, modules);
                finishSetup();
            }
        
            private void finishSetup() {
                if (Utils.isSimulation()) {
                    startSimThread();
                }
        
            this.sysIdManager = new SysIdRoutineManager(this);
            this.m_sysIdRoutineToApply = sysIdManager.getSelected();
            NetworkIO.set(KeyManager.SWERVE_KEY, "SysID", m_sysIdRoutineToApply.toString());
    
            configurePathPlanner();
            this.finder = new PoseFinder(this, SwerveConstants.pathConstraints);
            SmartDashboard.putData(KeyManager.FIELD_KEY, field);
    }

    /**
     * Consume datos de cualquier cámara o sensor de odometría (Limelight, QuestNav, etc.)
     * sin saber de dónde vienen.
     */
    public void consumeVisionData(VisionMsg visionData) {
        if (visionData.hasTarget && visionData.validPose) { 
            addVisionMeasurement(visionData.botPose, visionData.timestamp, visionData.stdDevs);
        }
    }

    @Override
    public void periodic() {
        // 1. Perspectiva del operador
        if (!m_hasAppliedOperatorPerspective || DriverStation.isDisabled()) {
            DriverStation.getAlliance().ifPresent(allianceColor -> {
                setOperatorPerspectiveForward(
                    allianceColor == Alliance.Red
                        ? SwerveConstants.kRedAlliancePerspectiveRotation
                        : SwerveConstants.kBlueAlliancePerspectiveRotation
                );
                m_hasAppliedOperatorPerspective = true;
            });
        }

        field.setRobotPose(getState().Pose);
    }

    private void configurePathPlanner() {
        try {
            RobotConfig config = RobotConfig.fromGUISettings();
            AutoBuilder.configure(
                () -> this.getState().Pose,      
                this::resetPose,         
                this::getChassisSpeeds, 
                (speeds, feedforwards) -> this.setControl(SwerveRequestFactory.pathPlannerRequest.withSpeeds(speeds)), 
                SwerveConstants.pathplannerPID,
                config,
                () -> {
                    var alliance = DriverStation.getAlliance();
                    return alliance.isPresent() && alliance.get() == DriverStation.Alliance.Red;
                },
                this
            );
        } catch (Exception e) {
            DriverStation.reportError("Fallo al configurar PathPlanner: " + e.getMessage(), true);
        }
    }

    public void setSysIdRoutine(SysIdRoutine routine){
        this.m_sysIdRoutineToApply = routine;
    }

    public Command sysIdQuasistatic(SysIdRoutine.Direction direction) {
        return m_sysIdRoutineToApply == null ? Commands.none() : m_sysIdRoutineToApply.quasistatic(direction);
    }

    public Command sysIdDynamic(SysIdRoutine.Direction direction) {
        return m_sysIdRoutineToApply == null ? Commands.none() : m_sysIdRoutineToApply.dynamic(direction);
    }

    public ChassisSpeeds getChassisSpeeds() {
        return getKinematics().toChassisSpeeds(getState().ModuleStates);
    }

    public Command applyRequest(Supplier<SwerveRequest> request) {
        return run(() -> this.setControl(request.get()));
    }

    public static Command moveXCommand(CommandSwerveDrivetrain swerve, double speed){
        return Commands.run(()-> {swerve.setControl(SwerveRequestFactory.driveRobotCentric.withVelocityX(speed).withVelocityY(0).withRotationalRate(0));}, swerve);
    }

    public static Command moveYCommand(CommandSwerveDrivetrain swerve, double speed){
        return Commands.run(()-> {swerve.setControl(SwerveRequestFactory.driveRobotCentric.withVelocityX(0).withVelocityY(speed).withRotationalRate(0));}, swerve);
    }

    public PoseFinder getPoseFinder(){
        return finder;
    }

    private void startSimThread() {
        m_lastSimTime = Utils.getCurrentTimeSeconds();
        m_simNotifier = new Notifier(() -> {
            final double currentTime = Utils.getCurrentTimeSeconds();
            double deltaTime = currentTime - m_lastSimTime;
            m_lastSimTime = currentTime;
            updateSimState(deltaTime, RobotController.getBatteryVoltage());
        });
        m_simNotifier.startPeriodic(SwerveConstants.kSimLoopPeriod);
    }

    @Override
    public void addVisionMeasurement(Pose2d visionRobotPoseMeters, double timestampSeconds) {
        super.addVisionMeasurement(visionRobotPoseMeters, Utils.fpgaToCurrentTime(timestampSeconds));
    }

    @Override
    public void addVisionMeasurement(Pose2d visionRobotPoseMeters, double timestampSeconds, Matrix<N3, N1> visionMeasurementStdDevs) {
        super.addVisionMeasurement(visionRobotPoseMeters, Utils.fpgaToCurrentTime(timestampSeconds), visionMeasurementStdDevs);
    }

    @Override
    public Optional<Pose2d> samplePoseAt(double timestampSeconds) {
        return super.samplePoseAt(Utils.fpgaToCurrentTime(timestampSeconds));
    }
}