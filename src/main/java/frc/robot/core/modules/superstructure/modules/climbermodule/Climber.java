package frc.robot.core.modules.superstructure.modules.climbermodule;

import java.util.function.Supplier;

import com.stzteam.forgemini.io.NetworkIO;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.configuration.KeyManager;
import frc.robot.configuration.KeyManager.CommonTables;
import frc.robot.configuration.KeyManager.CommonTables.Terminology;
import frc.robot.configuration.constants.ModuleConstants.ClimberConstants;
import frc.robot.core.modules.superstructure.modules.climbermodule.ClimberIO.ClimberInputs;
import frc.robot.configuration.factories.ClimberRequestFactory;

import frc.robot.core.requests.moduleRequests.ClimberRequest;
import mars.source.diagnostics.ActionStatus;
import mars.source.models.SubsystemBuilder;
import mars.source.models.Telemetry;
import mars.source.models.singlemodule.ModularSubsystem;
import mars.source.requests.Request;


public class Climber extends ModularSubsystem<ClimberInputs, ClimberIO>{

   

   public Climber(ClimberIO io){

        super(SubsystemBuilder.<ClimberInputs, ClimberIO>setup()
            .key(KeyManager.CLIMBER_KEY)
            .hardware(io, new ClimberInputs())
            .request(ClimberRequestFactory.idle)
            .telemetry(new ClimberTelemetry())
        );

        registerTelemetry(new ClimberTelemetry());
        this.setDefaultCommand(runRequest(()-> ClimberRequestFactory.idle));
    }

    public Command setControl(Supplier<ClimberRequest> request){
        return runRequest(request);
    }

     @Override
    public ClimberInputs getState(){
        return inputs;
    }


    public static class ClimberTelemetry extends Telemetry<ClimberInputs>{

        @Override
        public void telemeterize(ClimberInputs data, ActionStatus lastStatus) {
            NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.APPLIED_KEY + Terminology.VOLTS, data.appliedVolts);
            NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.TIMESTAMP_KEY, data.timestamp);

            if(lastStatus != null && lastStatus.code != null){
                NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.PAYLOAD_NAME_KEY, lastStatus.getPayload().name());
                NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.PAYLOAD_HEX_KEY, lastStatus.getPayload().colorHex());
                NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.PAYLOAD_MESSAGE_KEY, lastStatus.getPayload().message());
            }
            
        }
        
    }

    @Override
        public void absolutePeriodic(ClimberInputs inputs) {
        
    }

    }

