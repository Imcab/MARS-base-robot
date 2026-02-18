package frc.robot.configuration.factories;

import frc.robot.core.requests.moduleRequests.IndexerRequest;
import frc.robot.core.requests.moduleRequests.IndexerRequest.Idle;
import frc.robot.core.requests.moduleRequests.IndexerRequest.moveVoltage;

public class IndexerRequestFactory {
    public static final IndexerRequest.Idle idle = new Idle();
    public static final IndexerRequest.moveVoltage voltage = new moveVoltage();
    
}
