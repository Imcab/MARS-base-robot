package frc.robot.core.modules.superstructure.modules.turretmodule;

import java.util.function.Supplier;

import com.stzteam.forgemini.io.NetworkIO;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.configuration.KeyManager;
import frc.robot.core.modules.superstructure.modules.turretmodule.TurretIO.TurretInputs;
import frc.robot.core.requests.moduleRequests.TurretRequest;
import mars.source.diagnostics.ActionStatus;
import mars.source.models.SubsystemBuilder;
import mars.source.models.Telemetry;
import mars.source.models.singlemodule.ModularSubsystem;


public class Turret extends ModularSubsystem<TurretInputs, TurretIO> {

    private final Supplier<Pose2d> poseSupplier;
    private final Supplier<ChassisSpeeds> speedSupplier;

    public Turret(TurretIO io, Supplier<Pose2d> pose, Supplier<ChassisSpeeds> speeds) {
        super(SubsystemBuilder.<TurretInputs, TurretIO>setup()
            .key(KeyManager.TURRET_KEY)
            .hardware(io, new TurretInputs())
            .request(new TurretRequest.Idle())
            .telemetry(new TurretTelemetry())
        );
        
        this.poseSupplier = pose;
        this.speedSupplier = speeds;

        setDefaultCommand(runRequest(()-> new TurretRequest.Idle()));
    }

    public Command setControl(Supplier<TurretRequest> request){
        return runRequest(request);
    }

    @Override
    public void absolutePeriodic(TurretInputs data) {
        data.robotPose = poseSupplier.get();
        data.robotSpeed = speedSupplier.get();
    }

    public Rotation2d getRotation(){
        return inputs.angle;
    }

    public static class TurretTelemetry extends Telemetry<TurretInputs>{

        @Override
        public void telemeterize(TurretInputs data, ActionStatus lastStatus) {

            String table = KeyManager.TURRET_KEY;

            NetworkIO.set(table, "AppliedVoltage", data.appliedVolts);
            NetworkIO.set(table, "AngleDeg", data.angle.getDegrees());
            NetworkIO.set(table, "TargetAngleDeg", data.targetAngle.getDegrees());
            NetworkIO.set(table, "VelocityRPS", data.velocityRPS);
            NetworkIO.set(table, "LatencyMs", (Timer.getFPGATimestamp() - data.timestamp) * 1000);

            NetworkIO.set(table, "Status/Name", lastStatus.getPayload().name());
            NetworkIO.set(table, "Status/Message", lastStatus.getPayload().message());
            NetworkIO.set(table, "Status/Hex", lastStatus.getPayload().colorHex());
            
        }
        
    }
}