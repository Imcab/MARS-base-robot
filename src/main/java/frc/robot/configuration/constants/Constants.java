package frc.robot.configuration.constants;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.robot.configuration.constants.ModuleConstants.ArmConstants;

public class Constants {

    
    public static final double kSimLoopPeriod = 0.004;

    public static final Rotation2d kBlueAlliancePerspectiveRotation = Rotation2d.kZero;
    public static final Rotation2d kRedAlliancePerspectiveRotation = Rotation2d.k180deg;

    
    public static final Translation2d HUB_LOCATION = new Translation2d(4.5, 4);

    public static final InterpolatingDoubleTreeMap elevacionMap = new InterpolatingDoubleTreeMap();

    public static final Mechanism2d turretMechanism2d = new Mechanism2d(1, 1);

    public static final MechanismRoot2d root = turretMechanism2d.getRoot("pivote", 0.65, 0.35);
    
    public static final MechanismLigament2d armVisual = root.append(
            new MechanismLigament2d(
                "arm",
                ArmConstants.kArmLengthMeters,
                0, 6, new Color8Bit(Color.kYellow))
    );

    public static final MechanismLigament2d armTargetVisual = root.append(
            new MechanismLigament2d(
                "armTarget",
                ArmConstants.kArmLengthMeters,
                0, 3, new Color8Bit(Color.kRed))
        );


    public static final MechanismLigament2d flywheelVisual = armVisual.append(new MechanismLigament2d(
                "flywheelSpoke",
                0.1, // Longitud de la línea (radio visual)
                0,   // Ángulo inicial
                3,  // Grosor de la línea
                new Color8Bit(Color.kBlue)
    ));

    static {
            // elevacionMap.put(Distancia en metros, Ángulo del Angulador en grados);
            elevacionMap.put(1.0, 55.0); // Cerca del Hub, apunta alto
            elevacionMap.put(2.0, 48.0);
            elevacionMap.put(3.0, 42.5);
            elevacionMap.put(4.0, 36.0);
            elevacionMap.put(5.0, 31.0); // Lejos del Hub, apunta más plano (depende de tu shooter)
            // Nota: Estos números son inventados.

    }

    
}
