package frc.robot.core.modules.superstructure.modules.flywheelmodule;

import com.stzteam.forgemini.io.NetworkIO;

import edu.wpi.first.math.MathUtil;
import frc.robot.configuration.KeyManager;
import frc.robot.configuration.KeyManager.CommonTables;
import frc.robot.configuration.KeyManager.CommonTables.Terminology;
import frc.robot.configuration.factories.FlyWheelsRequestFactory;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO.FlyWheelInputs;
import mars.source.diagnostics.ActionStatus;
import mars.source.models.SubsystemBuilder;
import mars.source.models.Telemetry;
import mars.source.models.singlemodule.ModularSubsystem;

public class FlyWheel extends ModularSubsystem<FlyWheelInputs, FlyWheelIO>{

    public FlyWheel(FlyWheelIO io){
        super(SubsystemBuilder.<FlyWheelInputs, FlyWheelIO>setup()
            .key(KeyManager.FLYWHEEL_KEY)
            .hardware(io, new FlyWheelInputs())
            .request(FlyWheelsRequestFactory.Idle)
            .telemetry(new FlyWheelTelemetry()));

        this.setDefaultCommand(runRequest(()-> FlyWheelsRequestFactory.Idle));
    }

    @Override
    public FlyWheelInputs getState(){
        return inputs;
    }

    public boolean isAtTarget(double toleranceRPM) {
        return MathUtil.isNear(
            inputs.targetRPM, 
            inputs.velocityRPM, 
            toleranceRPM
        );
    }

    public static class FlyWheelTelemetry extends Telemetry<FlyWheelInputs>{

        @Override
        public void telemeterize(FlyWheelInputs data, ActionStatus lastStatus) {

            NetworkIO.set(KeyManager.FLYWHEEL_KEY, CommonTables.VELOCITY_KEY + Terminology.RPM, data.velocityRPM);
            NetworkIO.set(KeyManager.FLYWHEEL_KEY, CommonTables.APPLIED_KEY + Terminology.VOLTS, data.appliedVolts);
            NetworkIO.set(KeyManager.FLYWHEEL_KEY, CommonTables.TARGET_KEY + Terminology.RPM, data.targetRPM);
       
            if(lastStatus != null && lastStatus.code != null){
                NetworkIO.set(KeyManager.FLYWHEEL_KEY, CommonTables.PAYLOAD_NAME_KEY, lastStatus.getPayload().name());
                NetworkIO.set(KeyManager.FLYWHEEL_KEY, CommonTables.PAYLOAD_HEX_KEY, lastStatus.getPayload().colorHex());
                NetworkIO.set(KeyManager.FLYWHEEL_KEY, CommonTables.PAYLOAD_MESSAGE_KEY, lastStatus.getPayload().message());
            }
            
        }
    }

    @Override
    public void absolutePeriodic(FlyWheelInputs inputs) {
        
    }
    
}
