package frc.robot.core.requests.moduleRequests;

import java.util.function.Supplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.configuration.KeyManager.StatusCodes;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO.TurretInputs;
import frc.robot.diagnostics.TurretCode;
import mars.source.diagnostics.ActionStatus;
import mars.source.requests.Request;

public interface TurretRequest extends Request<TurretInputs, TurretIO> {

    public static class Idle implements TurretRequest {
        @Override
        public ActionStatus apply(TurretInputs data, TurretIO actor) {
            actor.stop();
            data.targetAngle = data.angle;
            return ActionStatus.of(TurretCode.IDLE, "Idle");
        }
    }

    public static class SysIdOpenLoop implements TurretRequest {
        private double m_volts = 0;

        public SysIdOpenLoop withVolts(double volts) {
            this.m_volts = volts;
            return this;
        }

        @Override
        public ActionStatus apply(TurretInputs data, TurretIO actor) {
            actor.setVoltage(m_volts);
            data.targetAngle = data.angle; 
            return ActionStatus.of(TurretCode.MANUAL_OVERRIDE, StatusCodes.MANUAL_STATUS + m_volts + "V");
        }
    }

    public static class Position implements TurretRequest {
        private Rotation2d m_targetAngle = new Rotation2d();
        private double toleranceDegrees = 1.0;
        
        public Position withTargetAngle(Rotation2d angle) {
            this.m_targetAngle = angle;
            return this;
        }

        public Position withTolerance(double tolerance) {
            this.toleranceDegrees = tolerance;
            return this;
        }

        @Override
        public ActionStatus apply(TurretInputs data, TurretIO actor) {
            data.targetAngle = m_targetAngle; 
            actor.setPosition(m_targetAngle);

            boolean isLocked = MathUtil.isNear(
                m_targetAngle.getDegrees(), 
                data.angle.getDegrees(), 
                toleranceDegrees
            );
            
            if (isLocked) {
                return ActionStatus.of(TurretCode.LOCKED, StatusCodes.TARGETREACHED_STATUS);
            } else {
                return ActionStatus.of(TurretCode.TRACKING, StatusCodes.TARGET_STATUS + Math.round(m_targetAngle.getDegrees()) + "°");
            }
        }
    }

    public static class LockOnTarget implements TurretRequest {
        
        private Supplier<Translation2d> targetSupplier;
        private double toleranceDegrees = 1.5;

        public LockOnTarget withTarget(Supplier<Translation2d> targetSupplier){
            this.targetSupplier = targetSupplier;
            return this;
        }

        public LockOnTarget withTolerance(double degrees) {
            this.toleranceDegrees = degrees;
            return this;
        }

        @Override
        public ActionStatus apply(TurretInputs data, TurretIO actor) {
 
            Translation2d currentTarget = targetSupplier.get();
            Translation2d turretPose = data.robotPose.getTranslation();

            Translation2d robotToTarget = currentTarget.minus(turretPose);
            
            Rotation2d fieldAngle = robotToTarget.getAngle();
            
            Rotation2d turretSetpoint = fieldAngle.minus(data.robotPose.getRotation());
 
            double cleanDegrees = MathUtil.inputModulus(turretSetpoint.getDegrees(), -180, 180);
            Rotation2d targetRot = Rotation2d.fromDegrees(cleanDegrees);

            data.targetAngle = targetRot;
            actor.setPosition(targetRot);

            boolean isLocked = MathUtil.isNear(
                targetRot.getDegrees(), 
                data.angle.getDegrees(), 
                toleranceDegrees,
                -180.0,
                180.0
            );
            
            if (isLocked) {
                return ActionStatus.of(TurretCode.LOCKED, StatusCodes.LOCK_STATUS);
            } else {
                return ActionStatus.of(TurretCode.TRACKING, StatusCodes.MOVING_STATUS);
            }
        }
    }
    
}