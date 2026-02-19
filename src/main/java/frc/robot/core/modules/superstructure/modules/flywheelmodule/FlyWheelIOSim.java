package frc.robot.core.modules.superstructure.modules.flywheelmodule;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.simulation.FlywheelSim;
import frc.robot.configuration.constants.Constants;
import frc.robot.configuration.constants.ModuleConstants.FlywheelConstants;

public class FlyWheelIOSim implements FlyWheelIO {

    private final FlywheelSim simWheel;
    
    // Controladores de velocidad en lugar de posición
    private final PIDController simController;
    private final SimpleMotorFeedforward feedforward;

    private double appliedVolts = 0.0;
    private boolean isClosedLoop = false;
    private double currentTargetRPM = 0.0;
    private final DCMotor gearbox;

    public FlyWheelIOSim() {
        this.gearbox = DCMotor.getNEO(1);
        double gearing = 1.5;
        double moi = FlywheelConstants.kMOI;

        var plant = LinearSystemId.createFlywheelSystem(gearbox, moi, gearing);
        this.simWheel = new FlywheelSim(plant, gearbox, gearing);

        // Inicializamos el PID (Solo kP, kI y kD en 0)
        this.simController = new PIDController(0., 0.0, 0.0);
        
        // Inicializamos el Feedforward (kS, kV, kA). 
        // Estos valores tendrás que afinarlos después.
        this.feedforward = new SimpleMotorFeedforward(0.1, 0.003, 0.0);
    }

    @Override
    public void updateInputs(FlyWheelInputs inputs) {
        double currentRPM = simWheel.getAngularVelocityRPM();

        // 1. Lógica de Control Cerrado (Closed Loop)
        if (isClosedLoop) {
            if (currentTargetRPM == 0.0) {
        // Mata el voltaje por completo si queremos estar quietos
        appliedVolts = 0.0;
        } else {
            double ffVolts = feedforward.calculate(currentTargetRPM);
            double pidVolts = simController.calculate(currentRPM, currentTargetRPM);
            appliedVolts = ffVolts + pidVolts;
        }
        }

        // 2. Restricciones físicas de la batería
        appliedVolts = MathUtil.clamp(appliedVolts, -12.0, 12.0);
        simWheel.setInputVoltage(appliedVolts);
        
        // 3. Avanzar el simulador 20ms
        simWheel.update(0.02);

        // 4. Actualizar las lecturas simuladas en la estructura de Inputs
        // Nota: Asegúrate de que los nombres de las variables coincidan con tu clase FlyWheelInputs
        currentRPM = simWheel.getAngularVelocityRPM();
        inputs.velocityRPM = currentRPM; 
        inputs.appliedVolts = appliedVolts;
        inputs.targetRPM = currentTargetRPM;

    }

    @Override
    public void applyOutput(double volts) {
        isClosedLoop = false;
        this.appliedVolts = volts;
    }

    @Override
    public void setTargetRPM(double rpm) {
        isClosedLoop = true;
        this.currentTargetRPM = rpm;
    }
}