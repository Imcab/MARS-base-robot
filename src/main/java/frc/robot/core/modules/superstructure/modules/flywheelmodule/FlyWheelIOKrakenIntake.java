package frc.robot.core.modules.superstructure.modules.flywheelmodule;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import frc.robot.configuration.constants.ModuleConstants.FlywheelConstants.IntakeWheelsConstants;
import frc.robot.configuration.constants.ModuleConstants.TunerConstants;

public class FlyWheelIOKrakenIntake implements FlyWheelIO {

    private final TalonFX intakeFlyWheels ;
    private TalonFXConfigurator FlyWheelsConfigurator;

    public FlyWheelIOKrakenIntake(){
        intakeFlyWheels = new TalonFX(IntakeWheelsConstants.IntakeWheels_ID,TunerConstants.kCANBus);
        FlyWheelsConfigurator = intakeFlyWheels.getConfigurator();

        configMotor();
    }

    public void configMotor(){
        var motorConfigs = new MotorOutputConfigs();

        motorConfigs.Inverted = IntakeWheelsConstants.invertedValue;
        motorConfigs.NeutralMode = NeutralModeValue.Brake;


        var limitConfigs = new CurrentLimitsConfigs();

        limitConfigs.StatorCurrentLimit = IntakeWheelsConstants.StatorCurrentLimit;
        limitConfigs.StatorCurrentLimitEnable = true; 

        limitConfigs.SupplyCurrentLimit = IntakeWheelsConstants.SupplyCurrentLimit;
        limitConfigs.SupplyCurrentLimitEnable = true;
        

        intakeFlyWheels.getConfigurator().apply(limitConfigs);

        FlyWheelsConfigurator.refresh(motorConfigs);
        FlyWheelsConfigurator.apply(motorConfigs);
    }

    @Override
    public void applyOutput(double volts){
        intakeFlyWheels.setVoltage(volts);
    }

    @Override
    public void setSpeed(double speed){
        intakeFlyWheels.set(speed);
    }

    @Override
    public void setTargetRPM(double RPM){}


    @Override
    public void updateInputs(FlyWheelInputs inputs){
        inputs.appliedVolts = intakeFlyWheels.getMotorVoltage().getValueAsDouble();
        inputs.velocityRPM = intakeFlyWheels.getVelocity().getValueAsDouble();
    }
}
