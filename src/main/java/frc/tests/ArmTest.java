// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.tests;

import com.stzteam.mars.test.MARSTest;
import com.stzteam.mars.test.TestRoutine;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.constants.Constants;
import frc.robot.core.modules.superstructure.modules.armmodule.Arm;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOKraken.ArmMODE;
import frc.robot.core.requests.moduleRequests.ArmRequestFactory;

@MARSTest(name = "Arm PID Motion Test")
public class ArmTest extends TestRoutine {
  private final Arm a;

  public ArmTest(Arm arm) {
    this.a = arm;
  }

  @Override
  public Command getRoutineCommand() {
    return Commands.sequence(
        run(
            ArmRequestFactory.setAngle()
                .withAngle(-40)
                .withMode(ArmMODE.kUP)
                .withTolerance(Constants.ARM_TOLERANCE),
            a),
        waitFor(() -> a.isAtTarget(Constants.ARM_TOLERANCE), 2),
        assertLessThan(calculateError(-40, a.getState().position), 2, "Error es muy alto"),
        delay(1),
        run(
            ArmRequestFactory.setAngle()
                .withAngle(-20)
                .withMode(ArmMODE.kDOWN)
                .withTolerance(Constants.ARM_TOLERANCE),
            a),
        waitFor(() -> a.isAtTarget(Constants.ARM_TOLERANCE), 2),
        assertLessThan(calculateError(-20, a.getState().position), 2, "Error es muy alto"),
        delay(1),
        run(
            ArmRequestFactory.setAngle()
                .withAngle(0)
                .withMode(ArmMODE.kDOWN)
                .withTolerance(Constants.ARM_TOLERANCE),
            a),
        waitFor(() -> a.isAtTarget(Constants.ARM_TOLERANCE), 2),
        assertLessThan(calculateError(0, a.getState().position), 2, "Error es muy alto"),
        delay(0.5),
        run(ArmRequestFactory.idle(), a));
  }
}
