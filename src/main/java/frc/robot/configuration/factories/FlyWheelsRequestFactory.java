package frc.robot.configuration.factories;

import frc.robot.configuration.constants.ModuleConstants.FlywheelConstants;
import frc.robot.core.requests.moduleRequests.FlyWheelRequest;
import frc.robot.core.requests.moduleRequests.FlyWheelRequest.Idle;
import frc.robot.core.requests.moduleRequests.FlyWheelRequest.InterpolateRPM;
import frc.robot.core.requests.moduleRequests.FlyWheelRequest.SetRPM;
import frc.robot.core.requests.moduleRequests.FlyWheelRequest.moveVoltage;

public class FlyWheelsRequestFactory {
    public static final FlyWheelRequest.Idle Idle = new Idle();
    public static final FlyWheelRequest.SetRPM RPMRequest = new SetRPM(0).withTolerance(FlywheelConstants.kRPMTolerance);
    public static final FlyWheelRequest.moveVoltage voltageRequest = new moveVoltage();
    public static final FlyWheelRequest.InterpolateRPM interpolateRPM = new InterpolateRPM();
}
