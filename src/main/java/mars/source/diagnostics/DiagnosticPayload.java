package mars.source.diagnostics;

public record DiagnosticPayload(
    String name,
    String message,
    String colorHex
) {

    public String toJson() {
        return String.format(
            "{\"name\":\"%s\", \"message\":\"%s\", \"colorHex\":\"%s\"}", 
            name, message, colorHex
        );
    }
}