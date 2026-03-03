package frc.robot.core.modules.superstructure.modules.armmodule;

import com.stzteam.features.unitprocessor.Unit;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import frc.robot.configuration.constants.ModuleConstants.ArmConstants;

public class ArmIOSim implements ArmIO {

    private final SingleJointedArmSim simArm;
    private final ProfiledPIDController simController;

    private double appliedVolts = 0.0;
    private boolean isClosedLoop = false;
    private double currentTargetAngle = 0.0;

  
    public ArmIOSim() {

        simArm = new SingleJointedArmSim(
            DCMotor.getNEO(1),
            ArmConstants.kGearRatio,
            SingleJointedArmSim.estimateMOI(ArmConstants.kArmLengthMeters, ArmConstants.kArmMassKg),
            ArmConstants.kArmLengthMeters,
            ArmConstants.kMinAngleRads,
            ArmConstants.kMaxAngleRads,
            true, 
            Units.degreesToRadians(15)
        );

        simController = new ProfiledPIDController(
            0.5, 0.0, 0.0,
            new TrapezoidProfile.Constraints(
                180.0,
                360.0
            )
        );

    }

    @Override
    public void updateInputs(ArmInputs inputs) {

        if (isClosedLoop) {
            double currentDegrees = Units.radiansToDegrees(simArm.getAngleRads());
            appliedVolts = simController.calculate(currentDegrees, currentTargetAngle);
        }

        appliedVolts = MathUtil.clamp(appliedVolts, -12.0, 12.0);
        simArm.setInputVoltage(appliedVolts);
        
        simArm.update(0.02);

        double simulatedDegrees = Units.radiansToDegrees(simArm.getAngleRads());

        inputs.position = simulatedDegrees;
        inputs.rotation = Rotation2d.fromDegrees(simulatedDegrees);
        inputs.targetAngle = currentTargetAngle;
        
    }

    @Override
    public void applyOutput(double volts) {
        isClosedLoop = false;
        this.appliedVolts = volts;
    }

    @Override
    public void setPosition(@Unit("Degrees") double angle) {
        isClosedLoop = true;
        this.currentTargetAngle = angle;
    }
}