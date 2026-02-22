package frc.robot.configuration.constants;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;

public class Constants {

    
    public static final double kSimLoopPeriod = 0.004;

    public static final Rotation2d kBlueAlliancePerspectiveRotation = Rotation2d.kZero;
    public static final Rotation2d kRedAlliancePerspectiveRotation = Rotation2d.k180deg;

    public static final Translation3d HUB_LOCATION = new Translation3d(4.63, 4.04, 1.9);

    public static final InterpolatingDoubleTreeMap INTERPOLATION_MAP = new InterpolatingDoubleTreeMap();

    static {
        // --- ZONA 1: FENDER / CONTACTO (Muy Cerca) ---
        // El Hub está bajo (1.25m). Si disparas recto aquí, le pegas al borde de plástico.
        // Necesitas "bombear" el FUEL hacia arriba para que caiga en el embudo.
        INTERPOLATION_MAP.put(1.00, 0.0);  // Hood totalmente abierto

        // --- ZONA 2: FUERA DEL TRENCH (Distancia Media) ---
        // A ~2.5m, ya tienes ángulo para disparar más directo.
        // Cerramos un poco el hood para ganar velocidad horizontal.
        INTERPOLATION_MAP.put(2.50, 18.0); 

        // --- ZONA 3: OUTPOST / DEPOT (Lejos) ---
        // A 4.5m+, el FUEL debe viajar rápido para no perder altura.
        // Cerramos el hood casi al máximo.
        INTERPOLATION_MAP.put(4.50, 32.0); 

        // --- ZONA 4: CROSS-FIELD (Máximo Rango) ---
        // Tiro casi horizontal para cruzar la cancha.
        INTERPOLATION_MAP.put(6.00, 40.0); 
    }

    public static enum MODES{
        AUTO_SHOOT,  //Esta funcion activa los motores del index, gira la torreta, angula el brazo. Y una vez que la torreta y brazo esten en posición, activa las fywheels para disparar como loco
        

    }

    
}
