package frc.robot.core.modules.superstructure.modules.turretmodule;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.configuration.constants.TurretConstants;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkLowLevel.MotorType;

public class TurretIOSparkMax implements TurretIO {
    private final SparkMax m_motor;
    private final AbsoluteEncoder m_encoder;

    private Rotation2d currentTargetAngle = new Rotation2d();

    public TurretIOSparkMax() {
        m_motor = new SparkMax(TurretConstants.kMotorId, MotorType.kBrushless);
        m_encoder = m_motor.getAbsoluteEncoder();
        
        var config = new SparkMaxConfig();
        
        var profiles = config.closedLoop;

        m_motor.setCANTimeout(250);

            try{

                profiles.pid(
                    TurretConstants.kP,
                    TurretConstants.kI,
                    TurretConstants.kD).
                    outputRange(TurretConstants.kMinOutput, TurretConstants.kMaxOutput);

                profiles.feedForward.kS(TurretConstants.kS).kV(TurretConstants.kV).kA(TurretConstants.kA);

                profiles.maxMotion.cruiseVelocity(TurretConstants.kCruiseVelocity).maxAcceleration(TurretConstants.kMaxAcc);

                config.
                    idleMode(IdleMode.kBrake).
                    inverted(TurretConstants.kMotorInverted).
                    smartCurrentLimit(TurretConstants.kCurrentLimit).
                    voltageCompensation(TurretConstants.kMaxVolts);
                
                config.softLimit.
                    forwardSoftLimit(TurretConstants.kUpperLimit).
                    forwardSoftLimitEnabled(true).
                    reverseSoftLimit(TurretConstants.kLowerLimit).
                    reverseSoftLimitEnabled(true);

                config.absoluteEncoder.
                    inverted(TurretConstants.kEncoderInverted).
                    positionConversionFactor(TurretConstants.kPositionFactor).
                    velocityConversionFactor(TurretConstants.kVelocityFactor);
        
                //Si usar encoder relativo configurarlo y poner esto
                //getMotor().getEncoder().setPosition(0);

                m_motor.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

            }
            
            finally{
                m_motor.setCANTimeout(0);
            }

    }

    @Override
    public void updateInputs(TurretInputs inputs) {
        inputs.angle = Rotation2d.fromRotations(m_encoder.getPosition());
        inputs.targetAngle = this.currentTargetAngle;
        inputs.velocityRPS = m_encoder.getVelocity();
        inputs.appliedVolts = m_motor.getAppliedOutput() * m_motor.getBusVoltage();
    }

    @Override
    public void setVoltage(double volts) {
        m_motor.setVoltage(volts);
    }

    @Override
    public void setPosition(Rotation2d angle) {
        this.currentTargetAngle = angle;
        m_motor.getClosedLoopController().setSetpoint(angle.getRotations(), ControlType.kMAXMotionPositionControl);
    }

    @Override
    public void stop() {
        m_motor.stopMotor();
    }

}