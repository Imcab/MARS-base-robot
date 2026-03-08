package frc.robot.configuration.constants.ModuleConstants;

import com.ctre.phoenix6.signals.InvertedValue;

public class FlywheelConstants {

    public class ShooterWheelsConstants{

        public static final int shooterLeaderID = 19;
        public static final int shooterFollowerID = 20;

        public static final double SupplyCurrentLimit = 90;
        public static final boolean SupplyCurrentLimitEnable = true;

        public static final double StatorCurrentLimit = 60;
        public static final boolean StatorCurrentLimitEnable = true;

        public static final double kRPMTolerance = 100;

        public static final double kS = 0;
        public static final double kV = 0.124;
        public static final double kP = 2.5;
        public static final double kI = 0;
        public static final double kD = 0;

        //----------------------------------------- FOR SIM //-----------------------------------------
        public static final double kGearing = 1;
        public static final double kMOI = 0.002;
        //----------------------------------------- FOR SIM //-----------------------------------------
        public static double idleVoltage = -1.5;

    }

    public class IntakeWheelsConstants{
        public static final int IntakeWheels_ID = 14;


        public static InvertedValue invertedValue = InvertedValue.CounterClockwise_Positive;
        public static double StatorCurrentLimit = 40;
        public static double SupplyCurrentLimit = 60;

    }

}
