package frc.robot.diagnostics;

import edu.wpi.first.wpilibj.util.Color;
import mars.source.diagnostics.DiagnosticPattern;
import mars.source.diagnostics.StatusCode;

public enum IndexerCode implements StatusCode{
    
    IDLE(Severity.OK, DiagnosticPattern.breathing(Color.kDarkGreen)),
    VOLTAGE(Severity.OK, DiagnosticPattern.solid(Color.kFirstBlue)),
    SPEED(Severity.OK, DiagnosticPattern.solid(Color.kYellow)),
    
    STOPED(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kRed));

    private final Severity severity;
    private final DiagnosticPattern pattern;

    IndexerCode(Severity severity, DiagnosticPattern pattern) {
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
