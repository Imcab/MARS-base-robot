package frc.robot.core.modules.superstructure.modules.armmodule;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.controls.MotionMagicExpoVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import frc.robot.configuration.constants.ModuleConstants.ArmConstants;

public class ArmIOKraken implements ArmIO{

    private TalonFX turretAngulator;
    private TalonFXConfigurator turretConfigurator;
    private TalonFXConfiguration talonFXConfigs;

    private MotionMagicExpoVoltage motionRequest;

    public enum ArmMODE {
        kUP,kDOWN
    }

    public ArmIOKraken(){
        turretAngulator = new TalonFX(ArmConstants.kId, CANBus.roboRIO());
        turretConfigurator = turretAngulator.getConfigurator();
        talonFXConfigs = new TalonFXConfiguration();
        motionRequest = new MotionMagicExpoVoltage(0);

        configMotion();
    }

    public void configMotion(){
        var motorConfigs = new MotorOutputConfigs();

        motorConfigs.Inverted = ArmConstants.invertedValue;
        motorConfigs.NeutralMode = NeutralModeValue.Brake;

        var limitConfigs = new CurrentLimitsConfigs();
        limitConfigs.StatorCurrentLimit = ArmConstants.currentLimit;
        limitConfigs.StatorCurrentLimitEnable = true;

        turretAngulator.getConfigurator().apply(limitConfigs);

        talonFXConfigs.Feedback.SensorToMechanismRatio = 17;
        talonFXConfigs.Feedback.RotorToSensorRatio = 1;
        
        var slot0Configs = talonFXConfigs.Slot0;

        var slot1Configs = talonFXConfigs.Slot1;

        slot0Configs.kS = ArmConstants.kS; // Add 0.25 V output to overcome static friction
        slot0Configs.kV = ArmConstants.kV; // A velocity target of 1 rps results in 0.12 V output
        slot0Configs.kA = ArmConstants.kA; // An acceleration of 1 rps/s requires 0.01 V output
        slot0Configs.kP = ArmConstants.kP; // A position error of 2.5 rotations results in 12 V output
        slot0Configs.kI = ArmConstants.kI; // no output for integrated error
        slot0Configs.kD = ArmConstants.kD; // A velocity error of 1 rps results in 0.1 V output

        slot1Configs.kS = 0; // Add 0.25 V output to overcome static friction
        slot1Configs.kV = 0; // A velocity target of 1 rps results in 0.12 V output
        slot1Configs.kA = 0; // An acceleration of 1 rps/s requires 0.01 V output
        slot1Configs.kP = 70; // A position error of 2.5 rotations results in 12 V output
        slot1Configs.kI = 0; // no output for integrated error
        slot1Configs.kD = 0.6; // A velocity error of 1 rps results in 0.1 V output

        turretConfigurator.refresh(motorConfigs);
        turretConfigurator.apply(talonFXConfigs);
        turretConfigurator.apply(motorConfigs);
        
    }
    

    @Override
    public void updateInputs(ArmInputs inputs) {

        var rotorPosSignal = turretAngulator.getPosition();

        var rotorPosRotations = rotorPosSignal.getValueAsDouble();
    
        inputs.position = rotorPosRotations * 360;
        inputs.rotation = Rotation2d.fromRotations(rotorPosRotations);

        inputs.timestamp = rotorPosSignal.getTimestamp().getLatency();

    }

    @Override
    public void applyOutput(double volts) {
        turretAngulator.setVoltage(volts);
    }

    @Override
    public void setPosition(double angle, ArmMODE mode) {

        switch (mode) {
            case kUP:
                turretAngulator.setControl(motionRequest.withPosition(Units.degreesToRotations(angle)).withSlot(0));
                break;
            case kDOWN:
                turretAngulator.setControl(motionRequest.withPosition(Units.degreesToRotations(angle)).withSlot(1));
                break;
        }

        
    }

    
}
