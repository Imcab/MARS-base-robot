package frc.robot.core.modules.superstructure.modules.climbermodule;

import java.util.function.Supplier;

import com.stzteam.features.dictionary.Dictionary.CommonTables;
import com.stzteam.features.dictionary.Dictionary.CommonTables.Terminology;
import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.models.Telemetry;
import com.stzteam.mars.models.singlemodule.ModularSubsystem;

import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.configuration.KeyManager;

import frc.robot.core.modules.superstructure.modules.climbermodule.ClimberIO.ClimberInputs;

import frc.robot.core.requests.moduleRequests.ClimberRequest;
import frc.robot.core.requests.moduleRequests.ClimberRequestFactory;

public class Climber extends ModularSubsystem<ClimberInputs, ClimberIO> {

    public Climber(ClimberIO io) {

        super(SubsystemBuilder.<ClimberInputs, ClimberIO>setup()
                .key(KeyManager.CLIMBER_KEY)
                .hardware(io, new ClimberInputs())
                .request(ClimberRequestFactory.idle())
                .telemetry(new ClimberTelemetry()));

        registerTelemetry(new ClimberTelemetry());
        this.setDefaultCommand(runRequest(() -> ClimberRequestFactory.idle()));
    }

    public Command setControl(Supplier<ClimberRequest> request) {
        return runRequest(request);
    }

    @Override
    public ClimberInputs getState() {
        return inputs;
    }

    public static class ClimberTelemetry extends Telemetry<ClimberInputs> {

        @Override
        public void telemeterize(ClimberInputs data, ActionStatus lastStatus) {
            NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.APPLIED_KEY + Terminology.VOLTS, data.appliedVolts);
            NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.TIMESTAMP_KEY, data.timestamp);

            if (lastStatus != null && lastStatus.code != null) {
                NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.PAYLOAD_NAME_KEY, lastStatus.getPayload().name());
                NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.PAYLOAD_HEX_KEY, lastStatus.getPayload().colorHex());
                NetworkIO.set(KeyManager.CLIMBER_KEY, CommonTables.PAYLOAD_MESSAGE_KEY,
                        lastStatus.getPayload().message());
            }

        }

    }

    @Override
    public void absolutePeriodic(ClimberInputs inputs) {

    }

}
