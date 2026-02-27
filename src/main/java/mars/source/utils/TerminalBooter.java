package mars.source.utils;

import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.networktables.StringSubscriber;
import edu.wpi.first.networktables.TimestampedBoolean;
import edu.wpi.first.networktables.TimestampedString;
import edu.wpi.first.wpilibj.RobotBase;

// Importes para la magia de ejecución
import mars.source.models.singlemodule.ModularSubsystem;
import mars.source.requests.Request;

public class TerminalBooter {

    public static final String MARS_VERSION = "V1.5.6";
    
    private static int moduleCount = 0;
    private static final Map<String, Boolean> mountedModules = new LinkedHashMap<>(); 
    private static final Map<String, List<String>> moduleRequestsMap = new HashMap<>();

    // ✨ LA BÓVEDA DE EJECUCIÓN: Aquí guardamos los objetos reales
    private static final Map<String, ModularSubsystem<?, ?>> activeSubsystems = new HashMap<>();
    private static final Map<String, Map<String, Request<?, ?>>> registeredRequests = new HashMap<>();

    private static StringPublisher marsConsoleStream;
    private static BooleanSubscriber syncSubscriber; 
    private static StringSubscriber requestQuerySubscriber;
    
    // ✨ EL NUEVO OÍDO PARA EJECUTAR COMANDOS
    private static StringSubscriber runRequestSubscriber;
    
    private static final Queue<String> logQueue = new ConcurrentLinkedQueue<>();
    private static int tickCounter = 0;

    public static void initNetworkStream() {
        NetworkTable marsTable = NetworkTableInstance.getDefault().getTable("MARS_GCS");
        marsConsoleStream = marsTable.getStringTopic("ConsoleLog").publish();
        
        syncSubscriber = marsTable.getBooleanTopic("Sync").subscribe(false);
        requestQuerySubscriber = marsTable.getStringTopic("GetRequests").subscribe("");
        
        // ✨ Escuchamos el nuevo canal de ejecución
        runRequestSubscriber = marsTable.getStringTopic("RunRequest").subscribe("");
    }

    public static void updatePeriodic() {
        if (NetworkTableInstance.getDefault().getConnections().length == 0) return; 

        // 1. SYNC
        TimestampedBoolean[] syncRequests = syncSubscriber.readQueue();
        if (syncRequests.length > 0) {
            broadcast("INFO", "SYS", "Sync request received. Re-broadcasting hardware tree...");
            for (Map.Entry<String, Boolean> entry : mountedModules.entrySet()) {
                String tag = entry.getValue() ? "Fallback" : "Hardware";
                broadcast("MOUNT", tag, entry.getKey());
            }
            printModuleSummary();
        }

        // 2. GET REQUESTS (mars request --get)
        TimestampedString[] queries = requestQuerySubscriber.readQueue();
        for (TimestampedString ts : queries) {
            String targetModule = ts.value;
            if (!targetModule.isEmpty()) {
                String foundKey = getModuleKeyIgnoreCase(targetModule);
                if (foundKey != null) {
                    String reqList = String.join(", ", moduleRequestsMap.get(foundKey));
                    broadcast("INFO", foundKey, "Available Requests: [ " + reqList + " ]");
                } else {
                    broadcast("WARN", "SYS", "Module '" + targetModule + "' not found.");
                }
            }
        }

        // ✨ 3. RUN REQUESTS (mars request --run)
        TimestampedString[] runCommands = runRequestSubscriber.readQueue();
        for (TimestampedString ts : runCommands) {
            String payload = ts.value; // Llega como "Climber:moveVoltage"
            if (payload.contains(":")) {
                String[] parts = payload.split(":");
                executeRemoteRequest(parts[0].trim(), parts[1].trim());
            }
        }

        // Desagüe de logs a 20ms
        tickCounter++;
        if (tickCounter >= 5) {
            if (!logQueue.isEmpty()) marsConsoleStream.set(logQueue.poll()); 
            tickCounter = 0; 
        }
    }

    // ✨ MOTOR DE INYECCIÓN DE REQUESTS (Ignora advertencias de tipos porque nosotros controlamos qué entra)
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void executeRemoteRequest(String module, String reqName) {
        String modKey = getModuleKeyIgnoreCase(module);
        
        if (modKey != null && activeSubsystems.containsKey(modKey) && registeredRequests.containsKey(modKey)) {
            ModularSubsystem sub = activeSubsystems.get(modKey);
            
            // Buscamos la request ignorando mayúsculas
            Request requestObj = null;
            String actualReqName = reqName;
            for (Map.Entry<String, Request<?, ?>> entry : registeredRequests.get(modKey).entrySet()) {
                if (entry.getKey().equalsIgnoreCase(reqName)) {
                    requestObj = entry.getValue();
                    actualReqName = entry.getKey();
                    break;
                }
            }

            if (requestObj != null) {
                sub.setRequest(requestObj);
                broadcast("REQUEST", sub.getName(), "GCS Override: Forced [" + actualReqName + "]");
            } else {
                broadcast("WARN", sub.getName(), "Request '" + reqName + "' not registered in this module.");
            }
        } else {
            broadcast("WARN", "SYS", "Cannot run request on '" + module + "'. Module not found.");
        }
    }

    private static String getModuleKeyIgnoreCase(String target) {
        for (String key : moduleRequestsMap.keySet()) {
            if (key.equalsIgnoreCase(target)) return key;
        }
        return null;
    }

    private static void broadcast(String type, String tag, String message) {
        String timestamp = Instant.now().toString();
        String safeMessage = message.replace("\"", "\\\""); 
        logQueue.add(String.format("{\"time\":\"%s\", \"type\":\"%s\", \"tag\":\"%s\", \"msg\":\"%s\"}", timestamp, type, tag, safeMessage));
    }

    public static void bootSequence() {
        broadcast("BOOT", "Core", "MARS Framework Starting...");
        broadcast("VERSION", "MARS", "Currently running on: " + MARS_VERSION);
        broadcast("INFO", "RobotMode", "Actuators on: " + (RobotBase.isReal() ? "RealIO" : "SimIO"));

        try {
            broadcast("GIT", "Branch", frc.robot.BuildConstants.GIT_BRANCH);
            broadcast("GIT", "Commit", frc.robot.BuildConstants.GIT_COMMIT);
            broadcast("BUILD", "Date", frc.robot.BuildConstants.BUILD_DATE);
        } catch (Exception e) {
            broadcast("WARN", "Git", "BuildConstants not found.");
        }
    }

    public static void registerModuleMount(String moduleName, boolean isFallback) {
        if (!mountedModules.containsKey(moduleName)) {
            mountedModules.put(moduleName, isFallback);
            moduleCount++;
        }
        broadcast("MOUNT", isFallback ? "Fallback" : "Hardware", moduleName);
    }

    // ✨ REGISTRO DEL SUBSISTEMA (Llamado desde ModularSubsystem)
    public static void registerSubsystem(ModularSubsystem<?, ?> subsystem) {
        activeSubsystems.put(subsystem.getName(), subsystem);
    }

    // ✨ REGISTRO DE REQUESTS CON SUS OBJETOS REALES (Llamado desde el Factory)
    public static void registerRemoteRequest(String moduleName, String reqName, Request<?, ?> requestObj) {
        // Guardamos el objeto real
        registeredRequests.computeIfAbsent(moduleName, k -> new HashMap<>()).put(reqName, requestObj);
        
        // Mantenemos la lista de Strings para el comando --get
        moduleRequestsMap.computeIfAbsent(moduleName, k -> new ArrayList<>());
        if (!moduleRequestsMap.get(moduleName).contains(reqName)) {
            moduleRequestsMap.get(moduleName).add(reqName);
        }
    }

    public static void printModuleSummary() {
        broadcast("INFO", "Core", "Successfully mounted " + moduleCount + " Hardware Modules.");
        broadcast("OK", "Robot", "Startup complete.");

    }

    public static void logInfo(String tag, String message) { broadcast("INFO", tag, message); }
    public static void logWarning(String tag, String message) { broadcast("WARN", tag, message); }
    public static void logError(String tag, String message) { broadcast("FATAL", tag, message); }
    public static void logRequest(String moduleName, String requestName) { broadcast("REQUEST", moduleName, requestName); }
    public static void logState(String moduleName, String stateName) { broadcast("STATE", moduleName, stateName); }
}