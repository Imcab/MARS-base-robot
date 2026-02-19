package frc.robot.core.modules.superstructure.modules.indexermodule;

import edu.wpi.first.math.MathUtil;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.simulation.FlywheelSim;
import frc.robot.configuration.constants.ModuleConstants.IndexerConstants;

public class IndexerIOSim implements IndexerIO{

    private final FlywheelSim simRoll;
    private final FlywheelSim simIndex;

    private double appliedVolts = 0.0;
    private final DCMotor gearbox;

    public IndexerIOSim() {
        this.gearbox = DCMotor.getNEO(1);
        double gearing = 1.5;
        double moi = IndexerConstants.kMOI;

        var plant = LinearSystemId.createFlywheelSystem(gearbox, moi, gearing);
        this.simIndex = new FlywheelSim(plant, gearbox, gearing);
        this.simRoll = new FlywheelSim(plant, gearbox, gearing);
    }

    @Override
    public void updateInputs(IndexerInputs inputs) {

        appliedVolts = MathUtil.clamp(appliedVolts, -12.0, 12.0);
        simIndex.setInputVoltage(appliedVolts);
        simRoll.setInputVoltage(appliedVolts);
        
        simIndex.update(0.02);
        simRoll.update(0.02);

        // 4. Actualizar las lecturas simuladas en la estructura de Inputs
        // Nota: Asegúrate de que los nombres de las variables coincidan con tu clase FlyWheelInputs
         
        inputs.appliedVoltsRoll = appliedVolts;
        inputs.appliedVoltsIndex = appliedVolts;

        inputs.velocityRoll = simRoll.getAngularVelocityRPM();
        inputs.velocityIndex = simIndex.getAngularVelocityRPM();


    }

    @Override
    public void applyOutput(double volts) {
        this.appliedVolts = volts;
    }

    @Override
    public void setSpeed(double speed) {}

    @Override
    public void stopAll() {}
    
}
