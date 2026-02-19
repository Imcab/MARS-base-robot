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
    
    private final SingleJointedArmSim simMotor;
    private final ProfiledPIDController simController;
    
    private double appliedVolts = 0.0;
    private boolean isClosedLoop = false;
    private Rotation2d currentTargetAngle = new Rotation2d();

    public TurretIOSim() {
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
            false, 
            0.0
        );

        // El controlador trabaja en ROTACIONES internamente
        simController = new ProfiledPIDController(
            TurretConstants.kP + 10, // Cuidado con el +15, prueba primero con la constante real
            TurretConstants.kI, 
            TurretConstants.kD,
            new TrapezoidProfile.Constraints(2.0, 4.0) 
        );
    }

    @Override
    public void updateInputs(TurretInputs inputs) {
        if (isClosedLoop) {
            // Convertimos la posición actual de radianes (sim) a rotaciones (PID)
            double currentPosRotations = Units.radiansToRotations(simMotor.getAngleRads());
            appliedVolts = simController.calculate(currentPosRotations);
        }

        appliedVolts = MathUtil.clamp(appliedVolts, -12.0, 12.0);
        simMotor.setInputVoltage(appliedVolts);
        simMotor.update(0.02); // Ciclo estándar de 20ms

        // Llenamos los inputs para el resto del código
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
        // Importante: El Goal debe estar en Rotaciones si el calculate usa rotaciones
        simController.setGoal(angle.getRotations()); 
    }

    @Override
    public void stop() {
        setVoltage(0.0);
    }
}