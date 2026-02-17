package frc.robot.configuration.constants;

import com.ctre.phoenix6.signals.InvertedValue;

import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
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

    public static final InterpolatingDoubleTreeMap elevacionMap = new InterpolatingDoubleTreeMap();

    static {
            // elevacionMap.put(Distancia en metros, Ángulo del Angulador en grados);
            elevacionMap.put(1.0, 55.0); // Cerca del Hub, apunta alto
            elevacionMap.put(2.0, 48.0);
            elevacionMap.put(3.0, 42.5);
            elevacionMap.put(4.0, 36.0);
            elevacionMap.put(5.0, 31.0); // Lejos del Hub, apunta más plano (depende de tu shooter)
            // Nota: Estos números son inventados.
    }

}
