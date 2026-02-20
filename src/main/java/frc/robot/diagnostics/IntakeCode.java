package frc.robot.diagnostics;

import edu.wpi.first.wpilibj.util.Color;
import mars.source.diagnostics.DiagnosticPattern;
import mars.source.diagnostics.StatusCode;

public enum IntakeCode implements StatusCode{

    IDLE(Severity.OK, DiagnosticPattern.breathing(Color.kDarkGreen)),
    ON_TARGET(Severity.OK, DiagnosticPattern.solid(Color.kFirstBlue)),
    
    MOVING_TO_ANGLE(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kYellow)),
    MANUAL_OVERRIDE(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kPurple)),
    
    OUT_OF_RANGE(Severity.ERROR, DiagnosticPattern.blinkFast(Color.kOrange));

    private final Severity severity;
    private final DiagnosticPattern pattern;

    IntakeCode(Severity severity, DiagnosticPattern pattern) {
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
