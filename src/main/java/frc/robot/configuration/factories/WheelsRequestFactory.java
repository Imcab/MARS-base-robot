package frc.robot.configuration.factories;

import frc.robot.core.requests.moduleRequests.WheelsRequest;
import frc.robot.core.requests.moduleRequests.WheelsRequest.Idle;
import frc.robot.core.requests.moduleRequests.WheelsRequest.moveVoltage;

public class WheelsRequestFactory {

    public static final WheelsRequest.Idle idle = new Idle();
    public static final WheelsRequest.moveVoltage voltage = new moveVoltage();
    
}
