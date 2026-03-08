package frc.robot.configuration.constants;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;

public class Constants {

    public static final double INTAKE_TOLERANCE = 2;
    public static final double TURRET_TOLERANCE = 4;
    public static final double FLYWHEEL_TOLERANCE = 50;
    public static final double ARM_TOLERANCE = 2;

    public static final double kSimLoopPeriod = 0.004;

    public static final Rotation2d kBlueAlliancePerspectiveRotation = Rotation2d.kZero;
    public static final Rotation2d kRedAlliancePerspectiveRotation = Rotation2d.k180deg;

    public static final Translation3d HUB_LOCATION = new Translation3d(4.63, 4.04, 1.9);

    public static final InterpolatingDoubleTreeMap INTERPOLATION_MAP = new InterpolatingDoubleTreeMap();

    public static final InterpolatingDoubleTreeMap RPM_MAP = new InterpolatingDoubleTreeMap();

    static {

        RPM_MAP.put(1.81592710, -2500.0); //Listo
        RPM_MAP.put(2.80519751, -2700.0);  //Listo
        RPM_MAP.put(3.79659061, -2900.0);
        RPM_MAP.put(4.95643191, -3400.0);
        RPM_MAP.put(5.86085769, -4000.0);
        RPM_MAP.put(6.86240250, -4100.0);
        RPM_MAP.put(7.80406202, -4500.0);
    }

    static {

        INTERPOLATION_MAP.put(1.815927105, 0.0);  // Hood totalmente abierto
        INTERPOLATION_MAP.put(2.80519751, -5.0); 
        INTERPOLATION_MAP.put(3.79659061, -10.0); 
        INTERPOLATION_MAP.put(4.95643191, -17.0); 
        INTERPOLATION_MAP.put(5.86085769, -18.0);
        INTERPOLATION_MAP.put(6.86240250, -19.7);
        INTERPOLATION_MAP.put(7.80406202, -20.0);
    }

    
}
