package frc.robot.diagnostics;

import edu.wpi.first.wpilibj.util.Color;
import mars.source.diagnostics.DiagnosticPattern;
import mars.source.diagnostics.StatusCode;

public enum ClimberCode implements StatusCode{
    
    IDLE(Severity.OK, DiagnosticPattern.breathing(Color.kDarkGreen)),

    CLIMBING (Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kYellow)),
    VOLTAGE(Severity.OK, DiagnosticPattern.solid(Color.kFirstBlue)),

    DOWN (Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kOrange));


    private final Severity severity;
    private final DiagnosticPattern pattern;

    ClimberCode(Severity severity, DiagnosticPattern pattern) {
        this.severity = severity;
        this.pattern = pattern;
    }

    @Override 
    public Severity getSeverity() { 
        return this.severity; 
    }

    @Override 
    public String getName() { 
        return this.name(); 
    }

    @Override 
    public DiagnosticPattern getVisualPattern() { 
        return this.pattern; 
    }
}
