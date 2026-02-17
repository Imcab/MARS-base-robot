package frc.robot.core.requests.moduleRequests;

import java.util.function.DoubleSupplier;

import frc.robot.configuration.constants.ModuleConstants.ArmConstants;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIO;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIO.ArmInputs;
import frc.robot.diagnostics.ArmCode;
import mars.source.diagnostics.ActionStatus;
import mars.source.requests.Request;

public interface ArmRequest extends Request<ArmInputs, ArmIO> {

    public static class Idle implements ArmRequest {
        @Override
        public ActionStatus apply(ArmInputs parameters, ArmIO actor) {
            actor.applyOutput(0);
            return ActionStatus.of(ArmCode.IDLE, "Motores en reposo");
        }
    }

    public static class InterpolateTarget implements ArmRequest {
        private DoubleSupplier distanciaMetros;
        private double tolerance = 1.0; // Grados de tolerancia por defecto
 
        public InterpolateTarget withDistance(DoubleSupplier target){
            this.distanciaMetros = target;
            return this;
        }

        public InterpolateTarget withTolerance(double tol){
            this.tolerance = tol;
            return this;
        }

        @Override
        public ActionStatus apply(ArmInputs parameters, ArmIO actor) {
            Double anguloDeseado = ArmConstants.elevacionMap.get(distanciaMetros.getAsDouble());

            if (anguloDeseado == null) {
                 actor.applyOutput(0);
                 return ActionStatus.of(ArmCode.OUT_OF_RANGE, "Distancia no tabulada");
            }

            actor.setPosition(anguloDeseado.doubleValue());
            parameters.targetAngle = anguloDeseado.doubleValue();

            //Lógica de llegada: ¿Ya estamos en el ángulo de la tabla?
            double error = Math.abs(parameters.position - anguloDeseado.doubleValue());
            if (error <= tolerance) {
                return ActionStatus.of(ArmCode.ON_TARGET, "Elevación lista (" + anguloDeseado + "°)");
            } else {
                return ActionStatus.of(ArmCode.MOVING_TO_ANGLE, "Ajustando elevación...");
            }
        }
    }

    public static class SetAngle implements ArmRequest {
        private double angle;
        private double tolerance = 1.0; // Grados de tolerancia por defecto

        public SetAngle(double initialAngle){
            this.angle = initialAngle;
        }

        public SetAngle withAngle(double angle){
            this.angle = angle;
            return this;
        }

        public SetAngle withTolerance(double tol){
            this.tolerance = tol;
            return this;
        }

        @Override
        public ActionStatus apply(ArmInputs parameters, ArmIO actor) {
            parameters.targetAngle = angle;
            actor.setPosition(angle);

            // Lógica de llegada: ¿Ya estamos en el ángulo fijo?
            double error = Math.abs(parameters.position - angle);
            if (error <= tolerance) {
                return ActionStatus.of(ArmCode.ON_TARGET, "Ángulo alcanzado");
            } else {
                return ActionStatus.of(ArmCode.MOVING_TO_ANGLE, "Moviendo a " + angle + "°");
            }
        }
    }
    
    public static class moveVoltage implements ArmRequest {
        double volts;

        public moveVoltage withVolts(double target){
            this.volts = target;
            return this;
        }
        
        @Override
        public ActionStatus apply(ArmInputs parameters, ArmIO actor) {
            actor.applyOutput(volts);
            // Avisamos que el PID está apagado y estamos en manual
            return ActionStatus.of(ArmCode.MANUAL_OVERRIDE, "Control manual: " + volts + "V");
        }
    }
}