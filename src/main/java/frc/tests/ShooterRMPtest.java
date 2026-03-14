// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.tests;

import com.stzteam.mars.test.MARSTest;
import com.stzteam.mars.test.TestRoutine;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.core.modules.superstructure.modules.flywheelmodule.FlyWheel;
import frc.robot.core.requests.moduleRequests.FlyWheelRequestFactory;

@MARSTest(name = "ShooterRMPtest")
public class ShooterRMPtest extends TestRoutine {
  FlyWheel s;

  public ShooterRMPtest(FlyWheel s) {
    this.s = s;
  }

  @Override
  public Command getRoutineCommand() {
    return Commands.sequence(
        run(FlyWheelRequestFactory.setRPM().toRPM(-3000).withTolerance(50), s),
        waitFor(() -> s.isAtTarget(50), 2.0),
        delay(4),
        run(FlyWheelRequestFactory.idleOutake(), s));
  }
}
