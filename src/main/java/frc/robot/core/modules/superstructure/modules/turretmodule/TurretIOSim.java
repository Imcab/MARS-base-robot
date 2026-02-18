package frc.robot.core.modules.superstructure.modules.turretmodule;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import frc.robot.configuration.constants.ModuleConstants.TurretConstants;

public class TurretIOSim implements TurretIO {
    
    // El motor físico virtual
    private final SingleJointedArmSim simMotor;
    
    // El "MAXMotion" virtual (Controlador de perfil trapezoidal)
    private final ProfiledPIDController simController;
    
    private double appliedVolts = 0.0;
    private boolean isClosedLoop = false;

    private Rotation2d currentTargetAngle = new Rotation2d();

    public TurretIOSim() {
        // 1. CREAMOS LA FÍSICA DEL MOTOR
        // Pasamos tu Constants directamente a las matemáticas de WPILib
        simMotor = new SingleJointedArmSim(
            DCMotor.getNEO(1),
            TurretConstants.kGearRatio,
            SingleJointedArmSim.estimateMOI(
                TurretConstants.kRadius.in(edu.wpi.first.units.Units.Meters), 
                TurretConstants.kMass.in(edu.wpi.first.units.Units.Kilograms)
            ),
            TurretConstants.kRadius.in(edu.wpi.first.units.Units.Meters),
            Units.rotationsToRadians(TurretConstants.kLowerLimit),
            Units.rotationsToRadians(TurretConstants.kUpperLimit),
            false, // ¿Simular gravedad? (Falso si la torreta gira horizontalmente)
            0.0
        );

        // 2. CREAMOS EL CEREBRO DEL MOTOR (Imitando al SparkMax)
        // Usamos tus mismas constantes de aceleración y velocidad
        simController = new ProfiledPIDController(
            TurretConstants.kP + 15, 
            TurretConstants.kI, 
            TurretConstants.kD,
            new TrapezoidProfile.Constraints(
                2.0, 
                4.0
            )
        );
    }

    @Override
    public void updateInputs(TurretInputs inputs) {
        // 1. CÁLCULO DE CONTROL (Si estamos en modo posición)
        if (isClosedLoop) {
            // El PID simula el trabajo del SparkMax calculando los voltios necesarios
            appliedVolts = simController.calculate(Units.radiansToRotations(simMotor.getAngleRads()));
        }

        // 2. AVANZAMOS EL TIEMPO EN LA SIMULACIÓN
        // Limitamos el voltaje a la batería (12V)
        appliedVolts = MathUtil.clamp(appliedVolts, -12.0, 12.0);
        simMotor.setInputVoltage(appliedVolts);
        
        // El ciclo normal de WPILib es de 20ms (0.02 segundos). 
        // Esto elimina la necesidad de tu Notifier viejo.
        simMotor.update(0.02);

        // 3. ESCRIBIMOS LA "MENTIRA" EN LOS INPUTS
        // El Subsistema leerá esto creyendo que viene de los encoders reales
        inputs.angle = Rotation2d.fromRadians(simMotor.getAngleRads());
        inputs.targetAngle = this.currentTargetAngle;
        inputs.velocityRPS = Units.radiansPerSecondToRotationsPerMinute(simMotor.getVelocityRadPerSec()) / 60;
        inputs.appliedVolts = appliedVolts;
    }

    @Override
    public void setVoltage(double volts) {
        isClosedLoop = false;
        this.appliedVolts = volts;
    }

    @Override
    public void setPosition(Rotation2d angle) {
        isClosedLoop = true;
        this.currentTargetAngle = angle;
        // Le damos la meta a nuestro MAXMotion falso
        simController.setGoal(angle.getRadians()); 
    }

    @Override
    public void stop() {
        setVoltage(0.0);
    }

  

}