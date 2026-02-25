package mars.source.diagnostics;

public interface StatusCode {
    
    enum Severity {
        OK,
        WARNING,
        ERROR,
        CRITICAL
    }

    Severity getSeverity();
    String getName();
    DiagnosticPattern getVisualPattern();
}