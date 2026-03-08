// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.core.modules.superstructure.composite;

import com.stzteam.mars.models.multimodules.CompositeData;

public class SuperstructureData extends CompositeData<SuperstructureData> {

  public boolean isAimedAtHub = false;
  public boolean isFlywheelReady = false;
  public boolean readyToShoot = false;
  public double distanceToHubMeter = 0.0;
}
