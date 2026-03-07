package frc.robot.core.requests.moduleRequests;

import java.util.function.DoubleSupplier;

import com.stzteam.features.dictionary.Dictionary.StatusCodes;
import com.stzteam.features.marsprocessor.RequestFactory;

import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.requests.Request;

import edu.wpi.first.math.MathUtil;

import frc.robot.configuration.constants.Constants;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIO;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIO.ArmInputs;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOKraken.ArmMODE;
import frc.robot.diagnostics.ArmCode;

@RequestFactory
public interface ArmRequest extends Request<ArmInputs, ArmIO> {

    public static class Idle implements ArmRequest {
        @Override
        public ActionStatus apply(ArmInputs parameters, ArmIO actor) {
            actor.applyOutput(0);
            return ActionStatus.of(ArmCode.IDLE, StatusCodes.IDLE_STATUS);
        }
    }

    public static class InterpolateTarget implements ArmRequest {
        private DoubleSupplier distanciaMetros;
        private double tolerance = 1.0;
        private ArmMODE mode;
 
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
            Double anguloDeseado = Constants.INTERPOLATION_MAP.get(distanciaMetros.getAsDouble());

            if (anguloDeseado == null) {
                 actor.applyOutput(0);
                 return ActionStatus.of(ArmCode.OUT_OF_RANGE, StatusCodes.ARM_TABULATED_ERROR);
            }

            if(anguloDeseado > 0){
                mode = ArmMODE.kUP;
            } else {
                mode = ArmMODE.kDOWN;
            }

            actor.setPosition(anguloDeseado.doubleValue(), mode);
            parameters.targetAngle = anguloDeseado.doubleValue();

            boolean isLocked = MathUtil.isNear(
                parameters.targetAngle, 
                parameters.position,
                tolerance
            );
            
            if (isLocked) {
                return ActionStatus.of(ArmCode.ON_TARGET, StatusCodes.TARGETREACHED_STATUS);
            } else {
                return ActionStatus.of(ArmCode.MOVING_TO_ANGLE, StatusCodes.TARGET_STATUS + StatusCodes.angleOf(parameters.targetAngle));
            }

        }
    }

    public static class SetAngle implements ArmRequest {
        private double angle;
        private double tolerance = 1.0; // Grados de tolerancia por defecto
        private ArmMODE mode;

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

        public SetAngle withMode(ArmMODE mode){
            this.mode = mode;
            return this;
        }

        @Override
        public ActionStatus apply(ArmInputs parameters, ArmIO actor) {
            parameters.targetAngle = angle;
            actor.setPosition(angle, mode);

            boolean isLocked = MathUtil.isNear(
                angle, 
                parameters.position,
                tolerance
            );

            if (isLocked) {
                return ActionStatus.of(ArmCode.ON_TARGET, StatusCodes.TARGETREACHED_STATUS);
            } else {
                return ActionStatus.of(ArmCode.MOVING_TO_ANGLE, StatusCodes.TARGET_STATUS + StatusCodes.angleOf(angle));
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
            return ActionStatus.of(ArmCode.MANUAL_OVERRIDE, StatusCodes.MANUAL_STATUS + StatusCodes.voltsOf(volts));
        }
    }
}