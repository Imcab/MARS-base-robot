package frc.robot.core.modules.superstructure.modules.armmodule;

import java.util.function.Supplier;

import com.stzteam.forgemini.io.NetworkIO;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.configuration.KeyManager;
import frc.robot.configuration.KeyManager.CommonTables;
import frc.robot.configuration.factories.ArmRequestFactory;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIO.ArmInputs;
import frc.robot.core.requests.moduleRequests.ArmRequest;
import mars.source.diagnostics.ActionStatus;
import mars.source.models.SubsystemBuilder;
import mars.source.models.Telemetry;
import mars.source.models.singlemodule.ModularSubsystem;


public class Arm extends ModularSubsystem<ArmIO.ArmInputs, ArmIO>{


    public Arm(ArmIO io){

        super(SubsystemBuilder.<ArmInputs, ArmIO>setup()
            .key(KeyManager.ARM_KEY)
            .hardware(io, new ArmInputs())
            .request(ArmRequestFactory.idle)
            .telemetry(new ArmTelemetry())
        );

        registerTelemetry(new ArmTelemetry());
        this.setDefaultCommand(runRequest(()-> ArmRequestFactory.idle));
    }

    public Command setControl(Supplier<ArmRequest> request){
        return runRequest(request);
    }

    @Override
    public void absolutePeriodic(ArmInputs inputs) {
        
    }

    public static class ArmTelemetry extends Telemetry<ArmIO.ArmInputs>{

        @Override
        public void telemeterize(ArmInputs data, ActionStatus lastStatus) {
            NetworkIO.set(KeyManager.ARM_KEY, CommonTables.DEGREES_KEY, data.position);
            NetworkIO.set(KeyManager.ARM_KEY, CommonTables.TIMESTAMP_KEY, data.timestamp);

            if(lastStatus != null && lastStatus.code != null){
                NetworkIO.set(KeyManager.ARM_KEY, CommonTables.PAYLOAD_NAME_KEY, lastStatus.getPayload().name());
                NetworkIO.set(KeyManager.ARM_KEY, CommonTables.PAYLOAD_HEX_KEY, lastStatus.getPayload().colorHex());
                NetworkIO.set(KeyManager.ARM_KEY, CommonTables.PAYLOAD_MESSAGE_KEY, lastStatus.getPayload().message());
            }
            
        }
        
    }

}
