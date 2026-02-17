package mars.source.diagnostics;

/**
 * Interfaz maestra para todos los códigos de estado del robot.
 */
public interface StatusCode {
    
    enum Severity {
        OK,        // Todo nominal (Verde/Azul)
        WARNING,   // Degradación o proceso incompleto (Amarillo/Naranja)
        ERROR,     // Fallo recuperable
        CRITICAL   // Peligro, detener el subsistema (Rojo/Morado)
    }

    Severity getSeverity();
    String getName();
    DiagnosticPattern getVisualPattern();
}