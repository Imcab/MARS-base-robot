package frc.robot.configuration.constants;

import static edu.wpi.first.units.Units.Kilograms;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Mass;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;

public class TurretConstants {

    //El PID de rev va de -1 a 1 (dutycycle)
    public static final double kMinOutput = -1;
    public static final double kMaxOutput = 1;

    //CONFIGURACION DE MAXMOTION
    public static final double kP = 0;
    public static final double kI = 0;
    public static final double kD = 0;

    public static final double kS = 0;
    public static final double kV = 0;
    public static final double kA = 0;

    public static final double kCruiseVelocity = 2.0;
    public static final double kMaxAcc = 4;

    //PEDIR
    public static final double kGearRatio = 15;

    public static final double kPositionFactor = 1 / kGearRatio; //Si NO ESTA DIRECTO A LA TORRETA
    //Si esta directo a la torreta usar (1:1):
    //public static final double kPositionFactor  = 1; 
    public static final double kVelocityFactor = kPositionFactor / 60; //RPM A RPS

    //------------VARIABLES DE VOLTAJE Y LIMITE DE CORRIENTE------------
    public static final int kCurrentLimit = 40;
    public static final double kMaxVolts = 12;

    //LÍMITE FÍSICO DE LA TORRETA, preguntar por si acaso (este valor me lo dió agus)
    public static final double kLowerLimit = Units.degreesToRotations(-200);
    public static final double kUpperLimit = Units.degreesToRotations(200);

    //PEDIR
    public static final int kMotorId = 50;

    public static final boolean kMotorInverted = false;
    public static final boolean kEncoderInverted = false;
    
    //Ubicación exacta del centro de la torreta respecto al robot:
    // X (Q metros): Significa que la torreta está Q cm hacia adelante del centro del robot.
    // Y (E metros): La torreta esta E metros en izquierda o derecha (0 centrada)
    // Z (W metros): El shooter está a W cm de altura del suelo.
    public static final Translation3d kMounting = new Translation3d(0.10, 0, 0.5);

    //VARIABLES DE SIMULACION (No las tienen que sacar), NO PASA NADA SI SE CAMBIAN, DEJARLAS TAL CUAL
    public static final Distance kRadius = Meters.of(0.15);
    public static final Mass kMass = Kilograms.of(5);

    public static final SysIdRoutine.Config kSysIdConfig = new SysIdRoutine.Config(

        Volts.per(Second).of(1.0),
   
        Volts.of(7.0),
        
        Seconds.of(10.0),
        
        state -> DataLogManager.log("Turret_SysIdState: " + state.toString())
    );

}
