package mars.source.diagnostics;

import edu.wpi.first.wpilibj.Timer;

/**
 * El reporte de salud generado por una Request en cada ciclo.
 */
public class ActionStatus {
    
    public final StatusCode code;
    public final String message;
    public final double timestamp;

    private ActionStatus(StatusCode code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = Timer.getFPGATimestamp();
    }

    // --- FACTORY METHODS ---

    public static ActionStatus of(StatusCode code, String message) {
        return new ActionStatus(code, message);
    }

    public static ActionStatus of(StatusCode code) {
        return new ActionStatus(code, code.getName());
    }

    // Helpers genéricos rápidos
    public static ActionStatus ok() { return new ActionStatus(GlobalCode.NOMINAL, "OK"); }
    public static ActionStatus warning(String msg) { return new ActionStatus(GlobalCode.WORKING, msg); }
    public static ActionStatus error(String msg) { return new ActionStatus(GlobalCode.HARDWARE_FAULT, msg); }

    // Evaluadores lógicos
    public boolean isDone() {
        return this.code.getSeverity() == StatusCode.Severity.OK;
    }
    
    public boolean isCritical() {
        return this.code.getSeverity() == StatusCode.Severity.CRITICAL;
    }

    public DiagnosticPayload getPayload() {
        return new DiagnosticPayload(
            this.code.getName(),
            this.message,
            this.code.getVisualPattern().evaluateHex()
        );
    }
}