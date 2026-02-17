package mars.source.diagnostics;

/**
 * Un contenedor ligero (DTO) para enviar diagnósticos completos en una sola línea.
 */
public record DiagnosticPayload(
    String name,
    String message,
    String colorHex
) {
    /**
     * Convierte el Struct a un formato JSON que Elastic u otros dashboards 
     * pueden leer y separar fácilmente si lo necesitan.
     */
    public String toJson() {
        return String.format(
            "{\"name\":\"%s\", \"message\":\"%s\", \"colorHex\":\"%s\"}", 
            name, message, colorHex
        );
    }
}