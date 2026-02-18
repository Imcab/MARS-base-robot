package frc.robot.diagnostics;

import edu.wpi.first.wpilibj.util.Color;
import mars.source.diagnostics.DiagnosticPattern;
import mars.source.diagnostics.StatusCode;

public enum FlywheelsCode implements StatusCode{
    
    IDLE(Severity.OK, DiagnosticPattern.breathing(Color.kDarkGreen)), //Respiración suave para reposo
    ON_TARGET(Severity.OK, DiagnosticPattern.solid(Color.kFirstBlue)), //Listo para disparar
    MOVING_TO_RPM(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kYellow)),
    MANUAL_OVERRIDE(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kPurple)); //Control por voltaje directo

    private final Severity severity;
    private final DiagnosticPattern pattern;

    FlywheelsCode(Severity severity, DiagnosticPattern pattern) {
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
