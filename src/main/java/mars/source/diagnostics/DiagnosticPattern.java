package mars.source.diagnostics;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.Color;

/**
 * Motor de renderizado visual para diagnósticos.
 * Calcula el color en formato Hexadecimal dependiendo del tiempo actual del robot.
 */
public class DiagnosticPattern {

    public enum Style {
        SOLID,          // Fijo
        BLINK_FAST,     // Parpadea rápido (Errores)
        BLINK_SLOW,     // Parpadea lento (Advertencias/Buscando)
        STROBE,         // Estrobo epiléptico (Pánico total/Crítico)
        BREATHING       // Pulsa suavemente (Nominal)
    }

    private final Color baseColor;
    private final Style style;

    public DiagnosticPattern(Color baseColor, Style style) {
        this.baseColor = baseColor;
        this.style = style;
    }

    // --- FACTORY METHODS RÁPIDOS ---
    public static DiagnosticPattern solid(Color color) { 
        return new DiagnosticPattern(color, Style.SOLID); 
    }
    
    public static DiagnosticPattern blinkFast(Color color) { 
        return new DiagnosticPattern(color, Style.BLINK_FAST); 
    }
    
    public static DiagnosticPattern blinkSlow(Color color) { 
        return new DiagnosticPattern(color, Style.BLINK_SLOW); 
    }

    // ¡Los que nos faltaban!
    public static DiagnosticPattern strobe(Color color) {
        return new DiagnosticPattern(color, Style.STROBE);
    }

    public static DiagnosticPattern breathing(Color color) {
        return new DiagnosticPattern(color, Style.BREATHING);
    }

    /**
     * Evalúa la función del tiempo y devuelve el color en Hexadecimal.
     * Ideal para mandar directamente a un Shape Widget en Elastic.
     */
    public String evaluateHex() {
        double time = Timer.getFPGATimestamp();
        Color currentColor;

        switch (style) {
            case BLINK_FAST:
                // Ciclo de 0.25s (encendido la mitad del tiempo)
                currentColor = (time % 0.25 < 0.125) ? baseColor : Color.kBlack;
                break;
                
            case BLINK_SLOW:
                // Ciclo de 1.0s
                currentColor = (time % 1.0 < 0.5) ? baseColor : Color.kBlack;
                break;

            case STROBE:
                // Ciclo ultra rápido y violento de 0.1s (10 parpadeos por segundo)
                currentColor = (time % 0.1 < 0.05) ? baseColor : Color.kBlack;
                break;
                
            case BREATHING:
                // Onda senoidal para un efecto de respiración suave (2 segundos de ciclo)
                double wave = (Math.sin(time * Math.PI) + 1.0) / 2.0;
                
                // Truco de Arquitecto: Le dejamos un 10% de brillo base para que 
                // no se vaya a negro total, se ve mucho más profesional.
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