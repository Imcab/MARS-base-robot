package mars.source.diagnostics;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.Color;

public class DiagnosticPattern {

    public enum Style {
        SOLID,
        BLINK_FAST,
        BLINK_SLOW,
        STROBE,
        BREATHING
    }

    private final Color baseColor;
    private final Style style;

    public DiagnosticPattern(Color baseColor, Style style) {
        this.baseColor = baseColor;
        this.style = style;
    }

    public static DiagnosticPattern solid(Color color) { 
        return new DiagnosticPattern(color, Style.SOLID); 
    }
    
    public static DiagnosticPattern blinkFast(Color color) { 
        return new DiagnosticPattern(color, Style.BLINK_FAST); 
    }
    
    public static DiagnosticPattern blinkSlow(Color color) { 
        return new DiagnosticPattern(color, Style.BLINK_SLOW); 
    }

    public static DiagnosticPattern strobe(Color color) {
        return new DiagnosticPattern(color, Style.STROBE);
    }

    public static DiagnosticPattern breathing(Color color) {
        return new DiagnosticPattern(color, Style.BREATHING);
    }

    public String evaluateHex() {
        double time = Timer.getFPGATimestamp();
        Color currentColor;

        switch (style) {
            case BLINK_FAST:

                currentColor = (time % 0.25 < 0.125) ? baseColor : Color.kBlack;
                break;
                
            case BLINK_SLOW:

                currentColor = (time % 1.0 < 0.5) ? baseColor : Color.kBlack;
                break;

            case STROBE:

                currentColor = (time % 0.1 < 0.05) ? baseColor : Color.kBlack;
                break;
                
            case BREATHING:

                double wave = (Math.sin(time * Math.PI) + 1.0) / 2.0;
                
                double intensity = 0.1 + (wave * 0.9); 
                
                currentColor = new Color(
                    baseColor.red * intensity, 
                    baseColor.green * intensity, 
                    baseColor.blue * intensity
                );
                break;
                
            case SOLID:
            default:
                currentColor = baseColor;
                break;
        }

        return currentColor.toHexString();
    }
}