package frc.robot.core.modules.superstructure.modules.armmodule;

import java.util.function.Supplier;

import com.stzteam.features.dictionary.Dictionary.CommonTables;
import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.models.Telemetry;
import com.stzteam.mars.models.singlemodule.ModularSubsystem;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.configuration.KeyManager;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIO.ArmInputs;
import frc.robot.core.requests.moduleRequests.ArmRequest;
import frc.robot.core.requests.moduleRequests.ArmRequestFactory;

public class Arm extends ModularSubsystem<ArmIO.ArmInputs, ArmIO> {

    public Arm(ArmIO io) {

        super(SubsystemBuilder.<ArmInputs, ArmIO>setup()
                .key(KeyManager.ARM_KEY)
                .hardware(io, new ArmInputs())
                .request(ArmRequestFactory.idle())
                .telemetry(new ArmTelemetry()));

        registerTelemetry(new ArmTelemetry());
        this.setDefaultCommand(runRequest(() -> ArmRequestFactory.idle()));
    }

    public Command setControl(Supplier<ArmRequest> request) {
        return runRequest(request);
    }

    @Override
    public void absolutePeriodic(ArmInputs inputs) {

    }

    @Override
    public ArmInputs getState() {
        return inputs;
    }

    public boolean isAtTarget(double toleranceDegrees) {
        return MathUtil.isNear(
                inputs.targetAngle,
                inputs.position,
                toleranceDegrees);
    }

    public static class ArmTelemetry extends Telemetry<ArmIO.ArmInputs> {

        @Override
        public void telemeterize(ArmInputs data, ActionStatus lastStatus) {

            if (lastStatus != null && lastStatus.code != null) {
                NetworkIO.set(KeyManager.ARM_KEY, CommonTables.PAYLOAD_NAME_KEY, lastStatus.getPayload().name());
                NetworkIO.set(KeyManager.ARM_KEY, CommonTables.PAYLOAD_HEX_KEY, lastStatus.getPayload().colorHex());
                NetworkIO.set(KeyManager.ARM_KEY, CommonTables.PAYLOAD_MESSAGE_KEY, lastStatus.getPayload().message());
            }

        }

    }

}
