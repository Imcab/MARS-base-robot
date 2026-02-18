package frc.robot.configuration.constants;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;

public class Constants {

    
    public static final double kSimLoopPeriod = 0.004;

    public static final Rotation2d kBlueAlliancePerspectiveRotation = Rotation2d.kZero;
    public static final Rotation2d kRedAlliancePerspectiveRotation = Rotation2d.k180deg;

    
    public static final Translation2d HUB_LOCATION = new Translation2d(4.5, 4);

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
