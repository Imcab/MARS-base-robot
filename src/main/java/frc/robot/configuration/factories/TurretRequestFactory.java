package frc.robot.configuration.factories;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.configuration.constants.Constants;
import frc.robot.core.requests.moduleRequests.TurretRequest;
import frc.robot.core.requests.moduleRequests.TurretRequest.Idle;
import frc.robot.core.requests.moduleRequests.TurretRequest.LockOnTarget;
import frc.robot.core.requests.moduleRequests.TurretRequest.Position;
import frc.robot.core.requests.moduleRequests.TurretRequest.SysIdOpenLoop;

public class TurretRequestFactory {
    
    public static final TurretRequest.Idle idle = new Idle();
    public static final TurretRequest.LockOnTarget targetLock = new LockOnTarget();
    public static final TurretRequest.Position toAngle = new Position();
    public static final TurretRequest.SysIdOpenLoop voltage = new SysIdOpenLoop();
    public static final TurretRequest.Position zeroTurret = toAngle.withTargetAngle(Rotation2d.kZero);

}
