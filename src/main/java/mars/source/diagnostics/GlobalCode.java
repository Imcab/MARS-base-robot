package mars.source.diagnostics;

import edu.wpi.first.wpilibj.util.Color;

public enum GlobalCode implements StatusCode {
    NOMINAL(Severity.OK, DiagnosticPattern.solid(Color.kGreen)),
    WORKING(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kYellow)),
    TIMEOUT(Severity.ERROR, DiagnosticPattern.blinkFast(Color.kOrange)),
    HARDWARE_FAULT(Severity.CRITICAL, DiagnosticPattern.blinkFast(Color.kRed));

    private final Severity severity;
    private final DiagnosticPattern pattern;

    GlobalCode(Severity severity, DiagnosticPattern pattern) {
        this.severity = severity;
        this.pattern = pattern;
    }

    @Override public Severity getSeverity() { return severity; }
    @Override public String getName() { return this.name(); }
    @Override public DiagnosticPattern getVisualPattern() { return pattern; }
}