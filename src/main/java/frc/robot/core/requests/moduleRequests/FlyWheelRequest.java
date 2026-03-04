package frc.robot.core.requests.moduleRequests;

import frc.robot.configuration.constants.Constants;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO.FlyWheelInputs;

import frc.robot.diagnostics.FlywheelsCode;

import com.stzteam.features.dictionary.Dictionary.CommonTables.Terminology;
import com.stzteam.features.dictionary.Dictionary.StatusCodes;
import com.stzteam.mars.diagnostics.ActionStatus;
import com.stzteam.mars.requests.Request;

import com.stzteam.features.marsprocessor.RequestFactory;

@RequestFactory
public interface FlyWheelRequest extends Request<FlyWheelInputs, FlyWheelIO>{

    public static class Idle implements FlyWheelRequest{

        @Override
        public ActionStatus apply(FlyWheelInputs parameters, FlyWheelIO actor) {
            actor.applyOutput(0);
            return ActionStatus.of(FlywheelsCode.IDLE, StatusCodes.IDLE_STATUS);
        }
        
    }

    public static class moveSpeed implements FlyWheelRequest {
        double speed;

        public moveSpeed withSpeed(double target){
            this.speed = target;
            return this;
        }
        
        @Override
        public ActionStatus apply(FlyWheelInputs parameters, FlyWheelIO actor) {
            actor.setSpeed(speed);
            return ActionStatus.of(FlywheelsCode.MANUAL_OVERRIDE, "Speed");
        }
    }

    public static class moveVoltage implements FlyWheelRequest {
        double volts;

        public moveVoltage withVolts(double target){
            this.volts = target;
            return this;
        }
        
        @Override
        public ActionStatus apply(FlyWheelInputs parameters, FlyWheelIO actor) {
            actor.applyOutput(volts);
            return ActionStatus.of(FlywheelsCode.MANUAL_OVERRIDE, StatusCodes.MANUAL_STATUS + StatusCodes.voltsOf(volts));
        }
    }

    public static class SetRPM implements FlyWheelRequest {
        private double rpm;
        private double tolerance = 1.0; // Grados de tolerancia por defecto

        public SetRPM(double rpm){
            this.rpm = rpm;
        }

        public SetRPM toRPM(double rpm){
            this.rpm = rpm;
            return this;
        }

        public SetRPM withTolerance(double tol){
            this.tolerance = tol;
            return this;
        }

        @Override
        public ActionStatus apply(FlyWheelInputs parameters, FlyWheelIO actor) {
            parameters.targetRPM = rpm;
            actor.setTargetRPM(rpm);

            boolean isAtTarget = MathUtil.isNear(rpm, parameters.velocityRPM, tolerance);
            
            if (isAtTarget) {
                return ActionStatus.of(FlywheelsCode.ON_TARGET, StatusCodes.TARGETREACHED_STATUS);
            } else {
                return ActionStatus.of(FlywheelsCode.MOVING_TO_RPM, StatusCodes.TARGET_STATUS + rpm + Terminology.RPM);
            }
        }
    }

    public static class InterpolateRPM implements FlyWheelRequest {
        private DoubleSupplier distanceMetersSupplier;
        private double toleranceRPM = 50.0;

        public InterpolateRPM withDistance(DoubleSupplier distanceSupplier) {
            this.distanceMetersSupplier = distanceSupplier;
            return this;
        }

        public InterpolateRPM withTolerance(double tol) {
            this.toleranceRPM = tol;
            return this;
        }

        @Override
        public ActionStatus apply(FlyWheelInputs data, FlyWheelIO actor) {

            double distance = distanceMetersSupplier.getAsDouble();
            
            double targetRPM = Constants.RPM_MAP.get(distance);
            
            data.targetRPM = targetRPM;
            actor.setTargetRPM(targetRPM);

            boolean isAtTarget = MathUtil.isNear(targetRPM, data.velocityRPM, toleranceRPM);
            
            if (isAtTarget) {
                return ActionStatus.of(FlywheelsCode.ON_TARGET, StatusCodes.TARGETREACHED_STATUS);
            } else {
                return ActionStatus.of(FlywheelsCode.MOVING_TO_RPM, StatusCodes.TARGET_STATUS + Math.round(targetRPM) + " RPM");
            }
        }
    }
}

