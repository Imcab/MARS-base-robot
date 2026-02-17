package frc.robot.helpers;

import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.SignalLogger;

import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.configuration.factory.SwerveRequestFactory;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;

public class SysIdRoutineManager {
    
    private final CommandSwerveDrivetrain sub;

    public final SysIdRoutine m_sysIdRoutineTranslation;
    public final SysIdRoutine m_sysIdRoutineSteer;
    public final SysIdRoutine m_sysIdRoutineRotation;

    public SysIdRoutineManager(CommandSwerveDrivetrain requirement){
        this.sub = requirement;

        /* Translation Routine */
        this.m_sysIdRoutineTranslation = new SysIdRoutine(
            new SysIdRoutine.Config(
                null,        
                Volts.of(4), 
                null,        
                state -> SignalLogger.writeString("SysIdTranslation_State", state.toString())
            ),
            new SysIdRoutine.Mechanism(
                output -> sub.setControl(SwerveRequestFactory.m_translationCharacterization.withVolts(output)),
                null,
                sub
            )
        );

        /* Steer Routine */
        this.m_sysIdRoutineSteer = new SysIdRoutine(
            new SysIdRoutine.Config(
                null,        
                Volts.of(7), 
                null,        
                state -> SignalLogger.writeString("SysIdSteer_State", state.toString())
            ),
            new SysIdRoutine.Mechanism(
                volts -> sub.setControl(SwerveRequestFactory.m_steerCharacterization.withVolts(volts)),
                null,
                sub
            )
        );

        /* Rotation Routine */
        this.m_sysIdRoutineRotation = new SysIdRoutine(
            new SysIdRoutine.Config(
                Volts.of(Math.PI / 6).per(Second),
                Volts.of(Math.PI),
                null, 
                state -> SignalLogger.writeString("SysIdRotation_State", state.toString())
            ),
            new SysIdRoutine.Mechanism(
                output -> {
                    sub.setControl(SwerveRequestFactory.m_rotationCharacterization.withRotationalRate(output.in(Volts)));
                    SignalLogger.writeDouble("Rotational_Rate", output.in(Volts));
                },
                null,
                sub
            )
        );
    }

    public SysIdRoutine getSelected(){
        return this.m_sysIdRoutineRotation;
    }

   
}