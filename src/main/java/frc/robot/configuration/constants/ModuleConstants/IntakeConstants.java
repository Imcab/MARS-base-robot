package frc.robot.configuration.constants.ModuleConstants;

import com.ctre.phoenix6.signals.InvertedValue;

import edu.wpi.first.math.util.Units;

public class IntakeConstants {

    public static final int Angulator_MOTOR_CAN_ID = 0;
    public static final int currentLimit = 80;
        
    public static final InvertedValue invertedValue = InvertedValue.Clockwise_Positive;

    public static final double kP = 0;
    public static final double kI = 0;
    public static final double kD = 0;

    public static final double kS = 0;
    public static final double kV = 0;
    public static final double kA = 0;

    public static final double kCruiseVelocity = 42;
    public static final double kMaxAcc = 80;

    public static final double kGearRatio = 20.0; 

    public static final double kIntakeLengthMeters = 0.7; // 30 cm de largo
    public static final double kIntakeMassKg = 7.0;       // 3 kg de peso

    public static final double kMinAngleRads = Units.degreesToRadians(0);
    public static final double kMaxAngleRads = Units.degreesToRadians(210);
    

    
}
