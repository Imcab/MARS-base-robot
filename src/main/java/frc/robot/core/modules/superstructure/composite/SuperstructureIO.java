package frc.robot.core.modules.superstructure.composite;

import com.stzteam.mars.models.multimodules.CompositeIO;

import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;

public class SuperstructureIO extends CompositeIO<SuperstructureData> {

    public SuperstructureIO(Turret turret, Arm arm, Intake intake, Indexer index, FlyWheel flywheelShooter, FlyWheel flywheelsIntake) {

        registerChild(turret);
        registerChild(arm);
        registerChild(intake);
        registerChild(index);
        registerChild(flywheelShooter);
        registerChild(flywheelsIntake);
    }

    @Override
    public void updateInputs(SuperstructureData inputs) {}

}