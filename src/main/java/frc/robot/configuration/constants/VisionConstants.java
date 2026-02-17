package frc.robot.configuration.constants;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

public class VisionConstants {

    public static final double MAX_VALID_DISTANCE_METERS = 4.0;
    public static final double MAX_ANGULAR_VELOCITY_DEG_PER_SEC = 720.0;
    public static final double ROTATION_STD_DEV = 9999999.0;
    public static final double MULTI_TAG_STD_DEV = 0.2;
    public static final double SINGLE_TAG_BASE_STD_DEV = 0.5;
    public static final double SINGLE_TAG_DISTANCE_MULTIPLIER = 0.1;
    public static final Matrix<N3, N1> DEFAULT_STD_DEVS = VecBuilder.fill(0.3, 0.3, ROTATION_STD_DEV);
    
}
