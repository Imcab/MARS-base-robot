package frc.robot.core.modules.superstructure.composite;

import frc.robot.configuration.KeyManager;
import frc.robot.configuration.constants.Constants;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.modules.superstructure.modules.intakemodule.Intake;
import frc.robot.core.modules.superstructure.modules.turretmodule.Turret;
import mars.source.models.multimodules.CompositeIO;

public class SuperstructureIO extends CompositeIO<SuperStructureData> {

    public SuperstructureIO(Turret turret, Arm arm, Intake intake, Indexer index, FlyWheel flywheel) {

        registerChild(turret);
        registerChild(arm);
        registerChild(intake);
        registerChild(index);
        registerChild(flywheel);
    }

    @Override
    public void updateInputs(SuperStructureData inputs) {

        Turret t = getChild(KeyManager.TURRET_KEY);
        Arm a = getChild(KeyManager.ARM_KEY);
        FlyWheel f = getChild(KeyManager.FLYWHEEL_KEY);
        Indexer idx = getChild(KeyManager.INDEX_KEY);

        //Aqui poner cosas asi
        inputs.isFlywheelReady = f.isAtTarget(Constants.FLYWHEEL_TOLERANCE);
    
        inputs.readyToShoot = inputs.isAimedAtHub && inputs.isFlywheelReady;
    }

}