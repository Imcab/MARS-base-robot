package frc.robot.core.modules.superstructure.modules.armmodule;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
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
import frc.robot.configuration.constants.ModuleConstants.ArmConstants;

public class ArmIOSim implements ArmIO {

    private final SingleJointedArmSim simArm;
    private final ProfiledPIDController simController;

    private double appliedVolts = 0.0;
    private boolean isClosedLoop = false;
    private double currentTargetAngle = 0.0;

    private final Mechanism2d mech; 
    private final MechanismRoot2d root;

    private final MechanismLigament2d arm; 
    private final MechanismLigament2d armTarget;
  
    public ArmIOSim() {

        simArm = new SingleJointedArmSim(
            DCMotor.getNEO(1),
            ArmConstants.kGearRatio,
            SingleJointedArmSim.estimateMOI(ArmConstants.kArmLengthMeters, ArmConstants.kArmMassKg),
            ArmConstants.kArmLengthMeters,
            ArmConstants.kMinAngleRads,
            ArmConstants.kMaxAngleRads,
            true, 
            Units.degreesToRadians(15) //Ángulo inicial (ej. descansa a 15 grados)
        );

        //(MAXMotion virtual)
        simController = new ProfiledPIDController(
            0.5, 0.0, 0.0, // kP, kI, kD (ajusta el kP para que responda bien)
            new TrapezoidProfile.Constraints(
                180.0, // Velocidad máxima: 180 grados por segundo
                360.0  //Aceleración máxima: 360 grados por segundo^2
            )
        );

        this.mech = new Mechanism2d(1.0, 1.0);
        this.root = mech.getRoot("Pivot", 0, 0.8);

        this.arm = root.append(
            new MechanismLigament2d(
                "arm",
                ArmConstants.kArmLengthMeters,
                0, 6, new Color8Bit(Color.kYellow))
        );
        
        this.armTarget = root.append(
            new MechanismLigament2d(
                "armTarget",
                ArmConstants.kArmLengthMeters,
                0, 3, new Color8Bit(Color.kRed))
        );

    }

    @Override
    public void updateInputs(ArmInputs inputs) {
        //(Si estamos en modo setPosition)
        if (isClosedLoop) {
            //El PID compara dónde estamos vs dónde queremos estar
            double currentDegrees = Units.radiansToDegrees(simArm.getAngleRads());
            appliedVolts = simController.calculate(currentDegrees, currentTargetAngle);
        }

        //RESTRICCIONES FÍSICAS Y AVANCE DEL TIEMPO
        //La batería del robot no da más de 12V
        appliedVolts = MathUtil.clamp(appliedVolts, -12.0, 12.0);
        simArm.setInputVoltage(appliedVolts);
        
        //Avanzamos el simulador 20 milisegundos (el ciclo de WPILib)
        simArm.update(0.02);

        double simulatedDegrees = Units.radiansToDegrees(simArm.getAngleRads());

        arm.setAngle(simulatedDegrees);
        armTarget.setAngle(currentTargetAngle);
        
        inputs.position = simulatedDegrees;
        inputs.rotation = Rotation2d.fromDegrees(simulatedDegrees);
        inputs.targetAngle = currentTargetAngle;
        
        SmartDashboard.putData(KeyManager.ARM_KEY + "/Mech", mech);
  
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
}