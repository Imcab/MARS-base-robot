package frc.robot.diagnostics;

import edu.wpi.first.wpilibj.util.Color;
import mars.source.diagnostics.DiagnosticPattern;
import mars.source.diagnostics.StatusCode;

public enum TurretCode implements StatusCode {
    
    IDLE(Severity.OK, DiagnosticPattern.breathing(Color.kDarkGreen)),
    LOCKED(Severity.OK, DiagnosticPattern.solid(Color.kFirstBlue)), //Objetivo centrado y listo para disparar
    
    TRACKING(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kYellow)), //Buscando el objetivo
    MANUAL_OVERRIDE(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kPurple)); //Control por voltaje puro (SysId)

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