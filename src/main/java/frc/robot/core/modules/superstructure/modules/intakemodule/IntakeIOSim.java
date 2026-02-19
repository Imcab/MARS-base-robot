package frc.robot.core.modules.superstructure.modules.intakemodule;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ProfiledPIDController;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.robot.configuration.KeyManager;

import frc.robot.configuration.constants.ModuleConstants.IntakeConstants;

public class IntakeIOSim implements IntakeIO{
    private final SingleJointedArmSim simIntake;
    private final ProfiledPIDController simController;

    private double appliedVolts = 0.0;
    private boolean isClosedLoop = false;
    private double currentTargetAngle = 0.0;

    private final Mechanism2d mech; 
    private final MechanismRoot2d root;

    private final MechanismLigament2d intake; 
    private final MechanismLigament2d intakeTarget;

    public IntakeIOSim(){

        simIntake = new SingleJointedArmSim(
            DCMotor.getNEO(1),
            IntakeConstants.kGearRatio,
            SingleJointedArmSim.estimateMOI(IntakeConstants.kIntakeLengthMeters, IntakeConstants.kIntakeMassKg),
            IntakeConstants.kIntakeLengthMeters,
            IntakeConstants.kMinAngleRads,
            IntakeConstants.kMaxAngleRads,
            true, 
            Units.degreesToRadians(30) //Ángulo inicial (ej. descansa a 15 grados)
        );

        simController = new ProfiledPIDController(
            0.5, 0.0, 0.0, // kP, kI, kD (ajusta el kP para que responda bien)
            new TrapezoidProfile.Constraints(
                180.0, // Velocidad máxima: 180 grados por segundo
                360.0  //Aceleración máxima: 360 grados por segundo^2
            )
        );

        this.mech = new Mechanism2d(1.0, 1.0);
        this.root = mech.getRoot("Pivot", 0, 0.8);

        this.intake = root.append(
            new MechanismLigament2d(
                "intake",
                IntakeConstants.kIntakeLengthMeters,
                0, 6, new Color8Bit(Color.kYellow))
        );
        
        this.intakeTarget = root.append(
            new MechanismLigament2d(
                "intakeTarget",
                IntakeConstants.kIntakeLengthMeters,
                0, 3, new Color8Bit(Color.kRed))
        );

    }

    @Override
    public void updateInputs(IntakeInputs inputs){
        if (isClosedLoop) {
            double currentDegrees = Units.radiansToDegrees(simIntake.getAngleRads());
            appliedVolts = simController.calculate(currentDegrees, currentTargetAngle);
        }


        appliedVolts = MathUtil.clamp(appliedVolts, -12.0, 12.0);
        simIntake.setInputVoltage(appliedVolts);
        
        simIntake.update(0.02);

        double simulatedDegrees = Units.radiansToDegrees(simIntake.getAngleRads());

        intake.setAngle(simulatedDegrees);
        intakeTarget.setAngle(currentTargetAngle);
        
        inputs.position = simulatedDegrees;
        inputs.targetAngle = currentTargetAngle;
        
        SmartDashboard.putData(KeyManager.INTAKE_KEY + "/Mech", mech);
    }
    
    @Override
    public void applyOutput(double volts) {
        isClosedLoop = false;
        this.appliedVolts = volts;
    }

    @Override
    public void setPosition(double angle) {
        isClosedLoop = true;
        this.currentTargetAngle = angle;
    }

    @Override
    public void stopAll(){}
    


}
