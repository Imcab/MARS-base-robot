package frc.robot.core.modules.superstructure.modules.flywheelmodule;

import java.util.function.Supplier;

import com.stzteam.features.dictionary.Dictionary.CommonTables;
import com.stzteam.features.dictionary.Dictionary.CommonTables.Terminology;
import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.models.Telemetry;
import com.stzteam.mars.models.singlemodule.ModularSubsystem;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO.FlyWheelInputs;

import frc.robot.core.requests.moduleRequests.FlyWheelRequest;
import frc.robot.core.requests.moduleRequests.FlyWheelRequestFactory;

public class FlyWheel extends ModularSubsystem<FlyWheelInputs, FlyWheelIO> {

    public static String subKey;

    public FlyWheel(FlyWheelIO io, String key) {
        super(SubsystemBuilder.<FlyWheelInputs, FlyWheelIO>setup()
                .key(key)
                .hardware(io, new FlyWheelInputs())
                .request(FlyWheelRequestFactory.idle())
                .telemetry(new FlyWheelTelemetry(key)));

        this.setDefaultCommand(runRequest(() -> FlyWheelRequestFactory.idle()));

        FlyWheel.subKey = key;
    }

    @Override
    public FlyWheelInputs getState() {
        return inputs;
    }

    public Command setControl(Supplier<FlyWheelRequest> request) {
        return runRequest(request);
    }

    public boolean isAtTarget(double toleranceRPM) {
        return MathUtil.isNear(
                inputs.targetRPM,
                inputs.velocityRPM,
                toleranceRPM);
    }

    public static class FlyWheelTelemetry extends Telemetry<FlyWheelInputs> {

        String key;

        public FlyWheelTelemetry(String key) {
            this.key = key;
        }

        @Override
        public void telemeterize(FlyWheelInputs data, ActionStatus lastStatus) {

            NetworkIO.set(key, CommonTables.VELOCITY_KEY + Terminology.RPM, data.velocityRPM);
            NetworkIO.set(key, CommonTables.APPLIED_KEY + Terminology.VOLTS, data.appliedVolts);
            NetworkIO.set(key, CommonTables.TARGET_KEY + Terminology.RPM, data.targetRPM);

            if (lastStatus != null && lastStatus.code != null) {
                NetworkIO.set(key, CommonTables.PAYLOAD_NAME_KEY, lastStatus.getPayload().name());
                NetworkIO.set(key, CommonTables.PAYLOAD_HEX_KEY, lastStatus.getPayload().colorHex());
                NetworkIO.set(key, CommonTables.PAYLOAD_MESSAGE_KEY, lastStatus.getPayload().message());
            }

        }
    }

    @Override
    public void absolutePeriodic(FlyWheelInputs inputs) {

    }

}
