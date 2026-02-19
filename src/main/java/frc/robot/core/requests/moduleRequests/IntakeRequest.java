package frc.robot.core.requests.moduleRequests;

import frc.robot.configuration.KeyManager.StatusCodes;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIO;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIO.IntakeInputs;

import frc.robot.diagnostics.IntakeCode;
import frc.robot.diagnostics.TurretCode;
import mars.source.diagnostics.ActionStatus;
import mars.source.requests.Request;

public interface IntakeRequest extends Request<IntakeInputs, IntakeIO>{

    public static class Idle implements IntakeRequest {
        @Override
        public ActionStatus apply(IntakeInputs data, IntakeIO actor) {
            actor.stopAll();
            return ActionStatus.of(TurretCode.IDLE, "Idle");
        }
    }

    public static class setAngle implements IntakeRequest {
        private double angle;
        private double tolerance = 1.0; // Grados de tolerancia por defecto

        public setAngle(double initialAngle){
            this.angle = initialAngle;
        }

        public setAngle withAngle(double angle){
            this.angle = angle;
            return this;
        }

        public setAngle Tolerance(double tolerance){
            this.tolerance = tolerance;
            return this;
        }

        @Override
        public ActionStatus apply(IntakeInputs parameters, IntakeIO actor) {
            parameters.targetAngle = angle;
            actor.setPosition(angle);

            double error = Math.abs(parameters.position - angle);
            if (error <= tolerance) {
                return ActionStatus.of(IntakeCode.ON_TARGET, StatusCodes.TARGETREACHED_STATUS);
            } else {
                return ActionStatus.of(IntakeCode.MOVING_TO_ANGLE, StatusCodes.TARGET_STATUS + StatusCodes.angleOf(angle));
            }


        }

    }

    public static class moveVoltage implements IntakeRequest { 
        private double voltage;

        public moveVoltage withVolts(double volts){
            this.voltage = volts;
            return this;
        }

        @Override
        public ActionStatus apply(IntakeInputs parameters, IntakeIO actor) {
            actor.applyOutput(voltage);
            return ActionStatus.of(IntakeCode.MANUAL_OVERRIDE, StatusCodes.MANUAL_STATUS + StatusCodes.voltsOf(voltage));
        }
    }
    
}
