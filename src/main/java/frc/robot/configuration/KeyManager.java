package frc.robot.configuration;

public class KeyManager {

    private KeyManager() {}

    public static final String AUTOCHOOSER_KEY = "AutoModes";
    public static final String SWERVE_KEY = "Swerve";
    public static final String FIELD_KEY = "Field";
    public static final String LIMELIGHT_KEY = "limelight";
    public static final String QUESTNAV_KEY = "Quest";
    public static final String TURRET_KEY = "Turret";
    public static final String ARM_KEY = "Arm";
    public static final String INDEX_KEY = "Indexer";
    public static final String FLYWHEEL_KEY = "Flywheel";
    public static final String INTAKE_KEY = "Intake";

    public static final class StatusCodes{

        private StatusCodes() {}

        public static final String IDLE_STATUS = "Idle";
        public static final String MANUAL_STATUS = "Manual: ";
        public static final String TARGET_STATUS = "Moving to: ";
        public static final String TARGETREACHED_STATUS = "Objective reached";
        public static final String MOVING_STATUS = "Adjusting...";
        public static final String LOCK_STATUS = "Locked in.";
        public static final String ARM_TABULATED_ERROR = "Non-Tabulated Distance";

        public static String voltsOf(double volts){
            return volts + "V";
        }

        public static String angleOf(double angle){
            return angle + "°";
        }

    }

    public static final class CommonTables{

        private CommonTables(){}

        public static String sPluralOf(String KEY){
            return KEY + Terminology.S;
        }

        public static final String SMARTDASHBOARD_KEY = "SmartDashboard";
        public static final String PAYLOAD_NAME_KEY = "Status/Name";
        public static final String PAYLOAD_MESSAGE_KEY = "Status/Message";
        public static final String PAYLOAD_HEX_KEY = "Status/Hex";
        public static final String DEGREES_KEY = "Degrees";
        public static final String RADIANS_KEY = "Radians";
        public static final String ROTATION_KEY = "Rotation";
        public static final String ANGLE_KEY = "Angle"; 
        public static final String TIMESTAMP_KEY = "Timestamp";   
        public static final String VELOCITY_KEY = "Velocity";
        public static final String RPM_KEY = "RPM";
        public static final String LATENCY_KEY = "Latency";
        public static final String APPLIED_KEY = "Applied";
        public static final String OUTPUT_KEY = "Output";
        public static final String INPUT_KEY = "Input";
        public static final String SECONDS_KEY = "Seconds";
        public static final String TIME_KEY = "Time";
        public static final String METERS_KEY = "Meters";
        public static final String TARGET_KEY = "Target";
        public static final String SETPOINT_KEY = "Setpoint";
        public static final String MODULE_KEY = "Module";
        public static final String STATE_KEY = "State";
        public static final String FREQUENCY_KEY = "Frequency";
        public static final String ODOMETRY_KEY = "Odometry";
        public static final String POSE_KEY = "Pose";
        public static final String OBJECTIVE_KEY = "Objective";
        public static final String GYRO_KEY = "Gyro";
        public static final String ROBOT_KEY = "Robot";
        public static final String SPEED_KEY = "Speed";
        public static final String POSITION_KEY = "Position";
        public static final String BATERY_KEY = "Battery";
        public static final String VIRTUAL_KEY = "Virtual";
        public static final String COUNT_KEY = "Count";
        public static final String TAG_KEY = "Tag";
        public static final String SIM_KEY = "Sim";
        public static final String REAL_KEY = "Real";
        public static final String VALID_KEY = "Valid";
        public static final String REQUEST_KEY = "Request";
        public static final String CURRENT_KEY = "Current";
        public static final String ACTIVE_KEY = "Active";
        public static final String RESULT_KEY = "Result";
     
        public static final class Terminology{
            public static final String MAX = "Max";
            public static final String MIN = "Min";
            public static final String S = "s";
            public static final String PER = "per";
            public static final String DEG = "Deg";
            public static final String RAD = "Rad";
            public static final String ROT = "Rot";
            public static final String MS = "ms";
            public static final String RPM = "rpm";
            public static final String RPS = "rps";
            public static final String SECONDS = "s";
            public static final String METERS = "m";
            public static final String MINUTES = "m";
            public static final String HUB = "hub";
            public static final String HAS = "has";
            public static final String IS = "is";
            public static final String IN = "in";
            public static final String AT = "at";
            public static final String ON = "on";
            public static final String ARE = "are";
            public static final String VOLTS = "Volts";
        }
    }

}
