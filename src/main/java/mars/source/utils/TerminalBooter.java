package mars.source.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.wpilibj.RobotBase;

public class TerminalBooter {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    public static final String MARS_VERSION = "V1.5.6";
    private static int moduleCount = 0;

    private static StringPublisher marsConsoleStream;
    
    private static final Queue<String> logQueue = new ConcurrentLinkedQueue<>();
    
    private static int tickCounter = 0;

    public static void initNetworkStream() {
        NetworkTable marsTable = NetworkTableInstance.getDefault().getTable("MARS_GCS");
        marsConsoleStream = marsTable.getStringTopic("ConsoleLog").publish();
    }

    public static void updatePeriodic() {

        if (NetworkTableInstance.getDefault().getConnections().length == 0) {
            return; 
        }

        tickCounter++;

        if (tickCounter >= 5) {
            if (!logQueue.isEmpty()) {
                marsConsoleStream.set(logQueue.poll());
            }
            tickCounter = 0;
        }
    }

    private static void broadcast(String type, String tag, String message) {
        String timestamp = LocalTime.now().format(TIME_FORMAT);
        String safeMessage = message.replace("\"", "\\\""); 
        
        String payload = String.format("{\"time\":\"%s\", \"type\":\"%s\", \"tag\":\"%s\", \"msg\":\"%s\"}", 
                                       timestamp, type, tag, safeMessage);
        
        logQueue.add(payload);
    }

    public static void bootSequence() {
        broadcast("BOOT", "Core", "MARS Framework Starting...");
        broadcast("VERSION", "MARS", "Currently running on: " + MARS_VERSION);
        
        String state = RobotBase.isReal() ? "RealIO" : "SimIO";
        broadcast("INFO", "RobotMode", "Actuators on: " + state);
        
        try {
            broadcast("GIT", "Branch", frc.robot.BuildConstants.GIT_BRANCH);
            broadcast("GIT", "Last Commit", frc.robot.BuildConstants.GIT_COMMIT);
            broadcast("BUILD", "Commit Date", frc.robot.BuildConstants.BUILD_DATE);
        } catch (Exception e) {
            broadcast("WARN", "Git", "BuildConstants not found.");
        }
    }

    public static void registerModuleMount(String moduleName) {
        moduleCount++;
        broadcast("MOUNT", "Hardware", moduleName + " module.");
    }

    public static void printModuleSummary() {
        broadcast("INFO", "Core", "Successfully mounted " + moduleCount + " Hardware Modules.");
        broadcast("OK", "Robot", "Startup complete. Ready to enable.");
    }

    public static void logInfo(String tag, String message) { broadcast("INFO", tag, message); }
    public static void logWarning(String tag, String message) { broadcast("WARN", tag, message); }
    public static void logError(String tag, String message) { broadcast("FATAL", tag, message); }
    public static void logRequest(String moduleName, String requestName) { broadcast("REQUEST", moduleName, requestName); }
    public static void logState(String moduleName, String stateName) { broadcast("STATE", moduleName, stateName); }
}