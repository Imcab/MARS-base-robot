package frc.robot.core.modules.superstructure.composite;

import mars.source.models.multimodules.CompositeData;

public class SuperstructureData extends CompositeData<SuperstructureData>{

    public boolean isAimedAtHub = false;
    public boolean isFlywheelReady = false;
    public boolean readyToShoot = false;
    public double distanceToHubMeter = 0.0;
    
}
