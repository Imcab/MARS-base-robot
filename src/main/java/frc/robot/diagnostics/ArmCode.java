package frc.robot.diagnostics;

import edu.wpi.first.wpilibj.util.Color;
import mars.source.diagnostics.DiagnosticPattern;
import mars.source.diagnostics.StatusCode;


public enum ArmCode implements StatusCode {
    
    IDLE(Severity.OK, DiagnosticPattern.breathing(Color.kDarkGreen)), //Respiración suave para reposo
    ON_TARGET(Severity.OK, DiagnosticPattern.solid(Color.kFirstBlue)), //Listo para disparar
    
    MOVING_TO_ANGLE(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kYellow)),
    MANUAL_OVERRIDE(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kPurple)), //Control por voltaje directo
    
    OUT_OF_RANGE(Severity.ERROR, DiagnosticPattern.blinkFast(Color.kOrange)), //Distancia fuera de la tabla
    GRAVITY_OVERLOAD(Severity.CRITICAL, DiagnosticPattern.strobe(Color.kRed)); //El motor no aguanta el peso

    private final Severity severity;
    private final DiagnosticPattern pattern;

    ArmCode(Severity severity, DiagnosticPattern pattern) {
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