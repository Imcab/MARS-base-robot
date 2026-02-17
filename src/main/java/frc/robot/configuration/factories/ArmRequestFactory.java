package frc.robot.configuration.factories;

import frc.robot.core.requests.moduleRequests.ArmRequest;
import frc.robot.core.requests.moduleRequests.ArmRequest.Idle;
import frc.robot.core.requests.moduleRequests.ArmRequest.InterpolateTarget;
import frc.robot.core.requests.moduleRequests.ArmRequest.SetAngle;
import frc.robot.core.requests.moduleRequests.ArmRequest.moveVoltage;

public class ArmRequestFactory {
    public static final ArmRequest.Idle idle = new Idle();
    public static final ArmRequest.moveVoltage voltage = new moveVoltage();
    public static final ArmRequest.SetAngle angle = new SetAngle(0);
    public static final ArmRequest.SetAngle home = angle;
    public static final ArmRequest.InterpolateTarget interpolate = new InterpolateTarget();
}
