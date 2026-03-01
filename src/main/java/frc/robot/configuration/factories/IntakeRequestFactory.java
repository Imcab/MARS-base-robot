package frc.robot.configuration.factories;

import frc.robot.configuration.KeyManager;
import frc.robot.core.requests.moduleRequests.IntakeRequest;
import frc.robot.core.requests.moduleRequests.IntakeRequest.Idle;
import frc.robot.core.requests.moduleRequests.IntakeRequest.moveVoltage;
import frc.robot.core.requests.moduleRequests.IntakeRequest.resetPosition;
import frc.robot.core.requests.moduleRequests.IntakeRequest.setAngle;
import mars.source.utils.TerminalBooter;

public class IntakeRequestFactory {

    public static final IntakeRequest.Idle idle = new Idle();
    public static final IntakeRequest.resetPosition reset = new resetPosition();
    public static final IntakeRequest.setAngle angle = new setAngle(0);
    public static final IntakeRequest.moveVoltage voltage = new moveVoltage();

   
    
}