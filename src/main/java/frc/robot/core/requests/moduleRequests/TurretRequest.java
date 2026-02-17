package frc.robot.core.requests.moduleRequests;

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
            data.targetAngle = data.angle; //Evita NPE y hace que el target fantasma se quede donde está
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
            data.targetAngle = data.angle; //En voltaje puro no hay target, seguimos donde estamos
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

            //Verificamos si ya llegamos a la posición fija
            double error = Math.abs(data.angle.minus(m_targetAngle).getDegrees());
            if (error < toleranceDegrees) {
                return ActionStatus.of(TurretCode.LOCKED, StatusCodes.TARGETREACHED_STATUS);
            } else {
                return ActionStatus.of(TurretCode.TRACKING, StatusCodes.TARGET_STATUS + Math.round(m_targetAngle.getDegrees()) + "°");
            }
        }
    }

    public static class LockOnTarget implements TurretRequest {
        private Translation2d targetLocation;
        private double fuelVelocityMPS = 10.0;
        private double toleranceDegrees = 1.0;

        public LockOnTarget(Translation2d targetLocation) {
            this.targetLocation = targetLocation;
        }

        public LockOnTarget withTarget(Translation2d targetLocation){
            this.targetLocation = targetLocation;
            return this;
        }

        public LockOnTarget withFuelVelocity(double mps) {
            this.fuelVelocityMPS = mps;
            return this;
        }

        public LockOnTarget withTolerance(double tolerance) {
            this.toleranceDegrees = tolerance;
            return this;
        }

        @Override
        public ActionStatus apply(TurretInputs data, TurretIO actor) {

            //Matemática Balística (Shoot-on-the-Move)
            Translation2d robotVelVector = new Translation2d(
                data.robotSpeed.vxMetersPerSecond,
                data.robotSpeed.vyMetersPerSecond
            ).rotateBy(data.robotPose.getRotation()); 

            Translation2d robotToTarget = targetLocation.minus(data.robotPose.getTranslation());
            double timeOfFlight = robotToTarget.getNorm() / fuelVelocityMPS;
            
            Translation2d virtualTarget = targetLocation.minus(robotVelVector.times(timeOfFlight));
            Rotation2d fieldAngle = virtualTarget.minus(data.robotPose.getTranslation()).getAngle();
            
            Rotation2d turretSetpoint = fieldAngle.minus(data.robotPose.getRotation());

            //Informamos a la telemetría y mandamos al motor
            data.targetAngle = turretSetpoint;
            actor.setPosition(turretSetpoint);

            //Verificamos si ya estamos apuntando al objetivo en movimiento
            double error = Math.abs(data.angle.minus(turretSetpoint).getDegrees());
            
            if (error < toleranceDegrees) {
                return ActionStatus.of(TurretCode.LOCKED, StatusCodes.LOCK_STATUS);
            } else {
                return ActionStatus.of(TurretCode.TRACKING, StatusCodes.MOVING_STATUS);
            }
        }
    }
}