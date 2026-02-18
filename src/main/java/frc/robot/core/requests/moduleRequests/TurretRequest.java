package frc.robot.core.requests.moduleRequests;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.configuration.KeyManager.StatusCodes;
import frc.robot.configuration.constants.ModuleConstants.TurretConstants;
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
        private double fuelVelocityMPS = 18.0; // Velocidad promedio de la nota (ajustar en pruebas)
        private double toleranceDegrees = 1.0;

        public LockOnTarget(Translation2d targetLocation) {
            this.targetLocation = targetLocation;
        }

        public LockOnTarget withTarget(Translation2d targetLocation){
            this.targetLocation = targetLocation;
            return this;
        }

        /**
         * Ajusta la velocidad de salida de la nota (Game Piece).
         * @param mps Metros por segundo (Meters Per Second).
         * Importante para el cálculo de tiempo de vuelo (Time of Flight).
         */
        public LockOnTarget withFuelVelocity(double mps) {
            this.fuelVelocityMPS = mps;
            return this;
        }

        /**
         * Define qué tan preciso debe ser el apuntado para considerar que estamos "Listos".
         * @param degrees Grados de error permitidos (Ej: 1.0 o 0.5).
         */
        public LockOnTarget withTolerance(double degrees) {
            this.toleranceDegrees = degrees;
            return this;
        }

        // ... otros builders (tolerance, velocity) ...

        @Override
        public ActionStatus apply(TurretInputs data, TurretIO actor) {
            
            Translation2d offsetRotated = TurretConstants.TURRET_OFFSET.rotateBy(data.robotPose.getRotation());
            
            // B. Sumamos: Posición Robot + Offset Rotado = Posición Torreta
            Translation2d turretPose = data.robotPose.getTranslation().plus(offsetRotated);

            // Vector de velocidad del robot (convertido a Field Centric)
            Translation2d robotVelVector = new Translation2d(
                data.robotSpeed.vxMetersPerSecond,
                data.robotSpeed.vyMetersPerSecond
            ).rotateBy(data.robotPose.getRotation()); 

            // Distancia desde la TORRETA al objetivo
            double distance = targetLocation.getDistance(turretPose);
            double timeOfFlight = distance / fuelVelocityMPS;
            
            // "Objetivo Virtual": Compensamos la velocidad que el robot le imprime a la nota
            // Si el robot se mueve a la derecha, apuntamos a la izquierda para cancelar.
            Translation2d virtualTarget = targetLocation.minus(robotVelVector.times(timeOfFlight));

            // Ángulo desde la TORRETA hacia el objetivo virtual
            Rotation2d angleFieldCentric = virtualTarget.minus(turretPose).getAngle();
            
            // Restamos la rotación del robot para obtener el ángulo local de la torreta
            Rotation2d turretSetpoint = angleFieldCentric.minus(data.robotPose.getRotation());
 
            data.targetAngle = turretSetpoint;
            actor.setPosition(turretSetpoint);

            double error = Math.abs(data.angle.minus(turretSetpoint).getDegrees());
            
            if (error < toleranceDegrees) {
                return ActionStatus.of(TurretCode.LOCKED, StatusCodes.LOCK_STATUS);
            } else {
                return ActionStatus.of(TurretCode.TRACKING, StatusCodes.MOVING_STATUS);
            }
        }
    }
    
    
}