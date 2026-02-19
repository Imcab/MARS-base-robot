package frc.robot.core.modules.superstructure.modules.intakemodule;

import java.util.function.Supplier;

import com.stzteam.forgemini.io.NetworkIO;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.configuration.KeyManager;
import frc.robot.configuration.KeyManager.CommonTables;
import frc.robot.configuration.factories.ArmRequestFactory;
import frc.robot.configuration.factories.IntakeRequestFactory;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm.ArmTelemetry;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIO;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIO.ArmInputs;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIO.IntakeInputs;
import frc.robot.core.requests.moduleRequests.ArmRequest;
import frc.robot.core.requests.moduleRequests.IntakeRequest;
import mars.source.diagnostics.ActionStatus;
import mars.source.models.SubsystemBuilder;
import mars.source.models.Telemetry;
import mars.source.models.singlemodule.ModularSubsystem;

public class Intake extends ModularSubsystem<IntakeInputs, IntakeIO>{

    public Intake(IntakeIO io){

        super(SubsystemBuilder.<IntakeInputs, IntakeIO>setup()
            .key(KeyManager.INTAKE_KEY)
            .hardware(io, new IntakeInputs())
            .request(IntakeRequestFactory.idle)
            .telemetry(new IntakeTelemetry())
        );

        registerTelemetry(new IntakeTelemetry());
        this.setDefaultCommand(runRequest(()-> IntakeRequestFactory.idle));
    }

     @Override
    public void absolutePeriodic(IntakeInputs inputs) {}

    public Command setControl(Supplier<IntakeRequest> request){
        return runRequest(request);
    }

    public static class IntakeTelemetry extends Telemetry<IntakeInputs>{

        @Override
        public void telemeterize(IntakeInputs data, ActionStatus lastStatus) {
            NetworkIO.set(KeyManager.INTAKE_KEY, CommonTables.DEGREES_KEY, data.position);
            NetworkIO.set(KeyManager.INTAKE_KEY, CommonTables.TIMESTAMP_KEY, data.timestamp);

            if(lastStatus != null && lastStatus.code != null){
                NetworkIO.set(KeyManager.INTAKE_KEY, CommonTables.PAYLOAD_NAME_KEY, lastStatus.getPayload().name());
                NetworkIO.set(KeyManager.INTAKE_KEY, CommonTables.PAYLOAD_HEX_KEY, lastStatus.getPayload().colorHex());
                NetworkIO.set(KeyManager.INTAKE_KEY, CommonTables.PAYLOAD_MESSAGE_KEY, lastStatus.getPayload().message());
            }
            
        }
        
    }
    
}
