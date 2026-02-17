package frc.robot.configuration.constants.ModuleConstants;

import com.ctre.phoenix6.signals.InvertedValue;

import edu.wpi.first.math.util.Units;

public class ArmConstants {

    public static final int kId = 0;

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

    public static final double kGearRatio = 50.0; 
    public static final double kArmLengthMeters = 0.3; // 30 cm de largo
    public static final double kArmMassKg = 3.0;       // 3 kg de peso

    public static final double kMinAngleRads = Units.degreesToRadians(0);
    public static final double kMaxAngleRads = Units.degreesToRadians(90);


}
