package frc.robot.core.modules.superstructure.modules.wheelsmodule;

import com.stzteam.forgemini.io.NetworkIO;


import frc.robot.configuration.KeyManager;
import frc.robot.configuration.KeyManager.CommonTables;

import frc.robot.configuration.factories.WheelsRequestFactory;

import frc.robot.core.modules.superstructure.modules.wheelsmodule.WheelsIO.WheelsInputs;

import mars.source.diagnostics.ActionStatus;
import mars.source.models.SubsystemBuilder;
import mars.source.models.Telemetry;
import mars.source.models.singlemodule.ModularSubsystem;

public class Wheels extends ModularSubsystem<WheelsInputs, WheelsIO>{

    public Wheels(WheelsIO io){

        super(SubsystemBuilder.<WheelsInputs, WheelsIO>setup()
            .key(KeyManager.WHEELS_KEY)
            .hardware(io, new WheelsInputs())
            .request(WheelsRequestFactory.idle)
            .telemetry(new WheelsTelemetry())
        );

        registerTelemetry(new WheelsTelemetry());
        this.setDefaultCommand(runRequest(()-> WheelsRequestFactory.idle));

    }

    public static class WheelsTelemetry extends Telemetry<WheelsInputs>{

        @Override
        public void telemeterize(WheelsInputs data, ActionStatus lastStatus) {
            NetworkIO.set(KeyManager.WHEELS_KEY, CommonTables.VELOCITY_KEY + "velocity", data.velocityRPM);
            NetworkIO.set(KeyManager.WHEELS_KEY, CommonTables.TIMESTAMP_KEY, data.timestamp);

            if(lastStatus != null && lastStatus.code != null){
                NetworkIO.set(KeyManager.WHEELS_KEY, CommonTables.PAYLOAD_NAME_KEY, lastStatus.getPayload().name());
                NetworkIO.set(KeyManager.WHEELS_KEY, CommonTables.PAYLOAD_HEX_KEY, lastStatus.getPayload().colorHex());
                NetworkIO.set(KeyManager.WHEELS_KEY, CommonTables.PAYLOAD_MESSAGE_KEY, lastStatus.getPayload().message());
            }
            
        }
        
    }

    @Override
    public void absolutePeriodic(WheelsInputs inputs) {
        
    }
    
}
