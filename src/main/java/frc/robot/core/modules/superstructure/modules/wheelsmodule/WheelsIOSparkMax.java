package frc.robot.core.modules.superstructure.modules.wheelsmodule;

import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import frc.robot.configuration.constants.ModuleConstants.WheelsConstants;

public class WheelsIOSparkMax implements WheelsIO{

    private final SparkMax wheels;
    private final RelativeEncoder encoder;

    public WheelsIOSparkMax() { 
        wheels = new SparkMax(WheelsConstants.WHEELS_CAN_ID, MotorType.kBrushless);
        encoder = wheels.getEncoder();
    }

    public void configMotor(){
        var config = new SparkMaxConfig();
        
        wheels.setCANTimeout(250);

        config.
            idleMode(IdleMode.kBrake).
            inverted(WheelsConstants.kMotorInverted).
            smartCurrentLimit(WheelsConstants.kCurrentLimit).
            voltageCompensation(WheelsConstants.kMaxVolts);

        config.absoluteEncoder.
            inverted(WheelsConstants.kEncoderInverted);

        wheels.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        wheels.setCANTimeout(0);
    }


    @Override
    public void applyOutput(double volts){
        wheels.setVoltage(volts);
    }

    @Override
    public void stop(){
        wheels.stopMotor();
    }

    @Override
    public void updateInputs(WheelsInputs inputs){
        inputs.appliedVolts = wheels.getAppliedOutput() * wheels.getBusVoltage();
        inputs.velocityRPM = encoder.getVelocity();

    }

}
