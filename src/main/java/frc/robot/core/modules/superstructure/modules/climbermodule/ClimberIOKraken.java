package frc.robot.core.modules.superstructure.modules.climbermodule;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import frc.robot.configuration.constants.ModuleConstants.ClimberConstants;
import frc.robot.configuration.constants.ModuleConstants.IntakeConstants;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;


public class ClimberIOKraken implements ClimberIO{

    private final TalonFX climber;
        private TalonFXConfigurator climberConfigurator;



    public ClimberIOKraken(){

        climber = new TalonFX(ClimberConstants.Climber_MOTOR_CAN_ID, "Canivore");

        var motorConfigs = new MotorOutputConfigs();

        motorConfigs.Inverted = ClimberConstants.invertedValue;
        motorConfigs.NeutralMode = NeutralModeValue.Brake;


        var limitConfigs = new CurrentLimitsConfigs();
        limitConfigs.StatorCurrentLimit = ClimberConstants.currentLimit;
        limitConfigs.StatorCurrentLimitEnable = false; 

        climber.getConfigurator().apply(limitConfigs);

        climberConfigurator.refresh(motorConfigs);
        climberConfigurator.apply(motorConfigs);


    }


  

    @Override
    public void setSpeed(double speed) {
        climber.set(speed);
    }

    @Override
        public void updateInputs(ClimberInputs inputs) {

            inputs.appliedVolts   = climber.getMotorVoltage().getValueAsDouble();

 }       
    
    @Override
    public void applyOutput(double volts) {
        climber.setVoltage(volts);
    }


}

