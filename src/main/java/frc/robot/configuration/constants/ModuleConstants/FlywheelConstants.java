package frc.robot.configuration.constants.ModuleConstants;

import com.ctre.phoenix6.signals.InvertedValue;

public class FlywheelConstants {
    public static final int ShooterWheels1_ID = 4;
    public static final int ShooterWheels2_ID = 5;

    public static final int IntakeWheels_ID = 0;

    public static final double kGearing = 1;
    public static final double kMOI = 0.002;
    public static final double kRPMTolerance = 250;

    public static InvertedValue IntakeWheelsInvertedValue = InvertedValue.CounterClockwise_Positive;
    public static double IntakeWheelscurrentLimit = 40;
}
