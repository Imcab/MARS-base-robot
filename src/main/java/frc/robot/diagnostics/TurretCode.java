package frc.robot.diagnostics;

import edu.wpi.first.wpilibj.util.Color;

import com.stzteam.mars.diagnostics.DiagnosticPattern;
import com.stzteam.mars.diagnostics.StatusCode;

public enum TurretCode implements StatusCode {
    
    IDLE(Severity.OK, DiagnosticPattern.breathing(Color.kDarkGreen)),
    LOCKED(Severity.OK, DiagnosticPattern.solid(Color.kFirstBlue)),
    
    RESET(Severity.OK, DiagnosticPattern.solid(Color.kDarkSalmon)),
    TRACKING(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kYellow)),
    MANUAL_OVERRIDE(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kPurple));

    private final Severity severity;
    private final DiagnosticPattern pattern;

    TurretCode(Severity severity, DiagnosticPattern pattern) {
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