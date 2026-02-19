package frc.robot.core.modules.superstructure.modules.intakemodule;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.controls.MotionMagicExpoVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.util.Units;

import frc.robot.configuration.constants.ModuleConstants.IntakeConstants;

public class IntakeIOKraken implements IntakeIO{

    private TalonFX angulator;
    private TalonFXConfigurator angulatorConfigurator;

    private MotionMagicExpoVoltage motionRequest;

    public IntakeIOKraken(){
        angulator = new TalonFX(IntakeConstants.Angulator_MOTOR_CAN_ID, "Canivore");

        motionRequest = new MotionMagicExpoVoltage(0);

        var motorConfigs = new MotorOutputConfigs();

        motorConfigs.Inverted = IntakeConstants.invertedValue;
        motorConfigs.NeutralMode = NeutralModeValue.Brake;


        var limitConfigs = new CurrentLimitsConfigs();
        limitConfigs.StatorCurrentLimit = IntakeConstants.currentLimit;
        limitConfigs.StatorCurrentLimitEnable = true;

        angulator.getConfigurator().apply(limitConfigs);

        angulatorConfigurator.refresh(motorConfigs);
        angulatorConfigurator.apply(motorConfigs);

        configMotion();
    }

    public void configMotion(){
        var talonFXConfigs = new TalonFXConfiguration();
        var slot0Configs = talonFXConfigs.Slot0;

        slot0Configs.kS = IntakeConstants.kS; // Add 0.25 V output to overcome static friction
        slot0Configs.kV = IntakeConstants.kV; // A velocity target of 1 rps results in 0.12 V output
        slot0Configs.kA = IntakeConstants.kA; // An acceleration of 1 rps/s requires 0.01 V output
        slot0Configs.kP = IntakeConstants.kP; // A position error of 2.5 rotations results in 12 V output
        slot0Configs.kI = IntakeConstants.kI; // no output for integrated error
        slot0Configs.kD = IntakeConstants.kD; // A velocity error of 1 rps results in 0.1 V output
    
        var motionMagicConfigs = talonFXConfigs.MotionMagic;
        motionMagicConfigs.MotionMagicCruiseVelocity = IntakeConstants.kCruiseVelocity; // Unlimited cruise velocity
        motionMagicConfigs.MotionMagicAcceleration = IntakeConstants.kMaxAcc;
    }

    @Override
    public void setPosition(double angle){
        angulator.setControl(motionRequest.withPosition(Units.degreesToRotations(angle)).withSlot(0));
    }

    @Override
    public void applyOutput(double voltage){
        angulator.setVoltage(voltage);
    }

    @Override
    public void stopAll(){
        angulator.stopMotor();
    }

    @Override
    public void updateInputs(IntakeInputs inputs){
        var rotorPosSignal = angulator.getRotorPosition();
        var rotorPosRotations = rotorPosSignal.getValueAsDouble();

        inputs.position = Units.rotationsToDegrees(rotorPosRotations);

        inputs.timestamp = rotorPosSignal.getTimestamp().getLatency();
    }
    

}
