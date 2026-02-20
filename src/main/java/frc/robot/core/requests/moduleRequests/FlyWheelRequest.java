package frc.robot.core.requests.moduleRequests;

import frc.robot.configuration.KeyManager.StatusCodes;
import edu.wpi.first.math.MathUtil;
import frc.robot.configuration.KeyManager.CommonTables.Terminology;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheelIO.FlyWheelInputs;
import frc.robot.diagnostics.ArmCode;
import frc.robot.diagnostics.FlywheelsCode;
import mars.source.diagnostics.ActionStatus;
import mars.source.requests.Request;

public interface FlyWheelRequest extends Request<FlyWheelInputs, FlyWheelIO>{

    public static class Idle implements FlyWheelRequest{

        @Override
        public ActionStatus apply(FlyWheelInputs parameters, FlyWheelIO actor) {
            actor.applyOutput(0);
            return ActionStatus.of(FlywheelsCode.IDLE, StatusCodes.IDLE_STATUS);
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
            return ActionStatus.of(ArmCode.MANUAL_OVERRIDE, StatusCodes.MANUAL_STATUS + StatusCodes.voltsOf(volts));
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
}

