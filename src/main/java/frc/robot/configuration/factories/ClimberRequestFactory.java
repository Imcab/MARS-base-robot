package frc.robot.configuration.factories;

import frc.robot.configuration.KeyManager;
import frc.robot.core.requests.moduleRequests.ClimberRequest;
import frc.robot.core.requests.moduleRequests.ClimberRequest.Idle;
import frc.robot.core.requests.moduleRequests.ClimberRequest.moveVoltage;
import mars.source.utils.TerminalBooter;

public class ClimberRequestFactory {
    public static final ClimberRequest.Idle idle = new Idle();
    public static final ClimberRequest.moveVoltage voltage = new moveVoltage();

}