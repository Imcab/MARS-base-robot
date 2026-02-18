package frc.robot.core.modules.superstructure.modules.indexermodule;

import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import frc.robot.configuration.constants.ModuleConstants.IndexerConstants;
import frc.robot.configuration.constants.ModuleConstants.TurretConstants;


public class IndexerSparkMax implements IndexerIO{

    private final SparkMax rollers;
    private final RelativeEncoder rollersEncoder;
    
    private final SparkMax indexMotor;
    private final RelativeEncoder indexEncoder;

    public IndexerSparkMax(){
        rollers = new SparkMax(IndexerConstants.ROLLERS_MOTOR_CAN_ID, MotorType.kBrushless);
        rollersEncoder = rollers.getEncoder();

        indexMotor = new SparkMax(IndexerConstants.INDEX_MOTOR_CAN_ID, MotorType.kBrushless);
        indexEncoder = indexMotor.getEncoder();


    }

    public void configureMotor(){
        var config = new SparkMaxConfig();
        
        rollers.setCANTimeout(250);
        indexMotor.setCANTimeout(250);

        config.
                idleMode(IdleMode.kBrake).
                inverted(TurretConstants.kMotorInverted).
                smartCurrentLimit(TurretConstants.kCurrentLimit).
                voltageCompensation(TurretConstants.kMaxVolts);

        config.absoluteEncoder.
                inverted(TurretConstants.kEncoderInverted);

        rollers.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        indexMotor.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        rollers.setCANTimeout(0);
        indexMotor.setCANTimeout(0);

    }

 
    @Override
    public void updateInputs(IndexerInputs inputs) {
        inputs.appliedVoltsRoll = rollers.getAppliedOutput() * rollers.getBusVoltage();
        inputs.velocityRoll = rollersEncoder.getVelocity();

        inputs.appliedVoltsIndex = indexMotor.getAppliedOutput() * indexMotor.getBusVoltage();
        inputs.velocityRoll = indexEncoder.getVelocity();

    }

    @Override
    public void applyOutput(double volts) {
        rollers.setVoltage(volts);
        indexMotor.setVoltage(volts);
    }

    @Override
    public void setSpeed(double speed){
        rollers.set(speed);
        indexMotor.set(speed);
    }

    @Override
    public void stopAll(){
        rollers.stopMotor();
        indexMotor.stopMotor();
    }
    


}
