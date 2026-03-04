package frc.robot.core.modules.superstructure.modules.indexermodule;

import java.util.function.Supplier;

import com.stzteam.features.dictionary.Dictionary.CommonTables;
import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.models.Telemetry;
import com.stzteam.mars.models.singlemodule.ModularSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.configuration.KeyManager;
import com.stzteam.features.marsprocessor.Fallback;
import frc.robot.core.modules.superstructure.modules.indexermodule.IndexerIO.IndexerInputs;

import frc.robot.core.requests.moduleRequests.IndexerRequest;
import frc.robot.core.requests.moduleRequests.IndexerRequestFactory;


public class Indexer extends ModularSubsystem<IndexerInputs,IndexerIO >{

    public Indexer(IndexerIO io){

        super(SubsystemBuilder.<IndexerInputs, IndexerIO>setup()
            .key(KeyManager.INDEX_KEY)
            .hardware(io, new IndexerInputs())
            .request(IndexerRequestFactory.idle())
            .telemetry(new IndexerTelemetry())
        );

        registerTelemetry(new IndexerTelemetry());
        this.setDefaultCommand(runRequest(()-> IndexerRequestFactory.idle()));
    }

    public Command setControl(Supplier<IndexerRequest> request){
        return runRequest(request);
    }

    @Override
    public IndexerInputs getState(){
        return inputs;
    }

    public static class IndexerTelemetry extends Telemetry<IndexerInputs>{

        @Override
        public void telemeterize(IndexerInputs data, ActionStatus lastStatus) {
            NetworkIO.set(KeyManager.INDEX_KEY, CommonTables.VELOCITY_KEY + "Index", data.velocityIndex);
            NetworkIO.set(KeyManager.INDEX_KEY, CommonTables.VELOCITY_KEY + "Roll", data.velocityRoll);
            NetworkIO.set(KeyManager.INDEX_KEY, CommonTables.TIMESTAMP_KEY, data.timestamp);

            if(lastStatus != null && lastStatus.code != null){
                NetworkIO.set(KeyManager.INDEX_KEY, CommonTables.PAYLOAD_NAME_KEY, lastStatus.getPayload().name());
                NetworkIO.set(KeyManager.INDEX_KEY, CommonTables.PAYLOAD_HEX_KEY, lastStatus.getPayload().colorHex());
                NetworkIO.set(KeyManager.INDEX_KEY, CommonTables.PAYLOAD_MESSAGE_KEY, lastStatus.getPayload().message());
            }
            
        }
        
    }



    @Override
    public void absolutePeriodic(IndexerInputs inputs) {
        
    }
    
}
