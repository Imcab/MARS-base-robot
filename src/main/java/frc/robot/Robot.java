// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot;

import com.revrobotics.util.StatusLogger;
import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.mars.builder.Environment;
import com.stzteam.mars.builder.Environment.RunMode;
import com.stzteam.mars.models.containers.IRobotContainer;
import com.stzteam.mars.test.TestScheduler;
import com.stzteam.mars.utils.GCSConsole;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.configuration.Manifest;
import frc.robot.utils.LimelightHelpers;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private final IRobotContainer m_robotContainer;
  public final String limelightName = "limelight";
  private double matchTime;
  private double teleopTimeElapsed;
  private int shiftTimer;
  private String currentShift = "ESPERANDO...";

  public Robot() {

    Environment.setMode(Manifest.CURRENT_MODE);

    if (Manifest.HAS_MARS_GCS) {

      GCSConsole.bootSequence();
    }

    DriverStation.silenceJoystickConnectionWarning(true);

    m_robotContainer = new RobotContainer();

    if (Manifest.HAS_MARS_GCS) {
      GCSConsole.printModuleSummary();
    }

    if (Environment.getMode() == RunMode.REAL) {
      CameraServer.startAutomaticCapture();
    }

    NetworkIO.set("System", "IO", Environment.getMode().name());
    NetworkIO.set("System", "isOnSim", RobotBase.isSimulation());

    StatusLogger.disableAutoLogging();
  }

  @Override
  public void robotPeriodic() {

    CommandScheduler.getInstance().run();
    m_robotContainer.updateNodes();

    matchTime = Math.round(DriverStation.getMatchTime());
    teleopTimeElapsed = 145.0 - matchTime;

    if (DriverStation.isTeleop()) {
      if (matchTime > 130 & matchTime <= 140) {
        currentShift = "TRANSITION SHIFT";
        shiftTimer = 10 - (int) (teleopTimeElapsed % 10);
      } else if (matchTime > 105 & matchTime <= 130) {
        currentShift = "SHIFT 1";
        shiftTimer = 25 - (int) (teleopTimeElapsed % 25);
      } else if (matchTime > 80 & matchTime <= 105) {
        currentShift = "SHIFT 2";
        shiftTimer = 25 - (int) (teleopTimeElapsed % 25);
      } else if (matchTime > 55 & matchTime <= 80) {
        currentShift = "SHIFT 3";
        shiftTimer = 25 - (int) (teleopTimeElapsed % 25);
      } else if (matchTime > 30 & matchTime <= 55) {
        currentShift = "SHIFT 4";
        shiftTimer = 25 - (int) (teleopTimeElapsed % 25);
      } else if (matchTime > 0 & matchTime <= 30) {
        currentShift = "END GAME";
        shiftTimer = 30 - (int) (teleopTimeElapsed % 30);
      }
    } else if (DriverStation.isAutonomous()) {
      currentShift = "AUTO SHIFT";
    }

    int minutes = (int) (matchTime / 60);
    int seconds = (int) (matchTime % 60);
    String formattedTime = String.format("%d:%02d", minutes, seconds);

    NetworkIO.set("MatchData", "Match Time", formattedTime);
    NetworkIO.set("MatchData", "Shift Time", shiftTimer);
    NetworkIO.set("MatchData", "Raw Time", matchTime);
    NetworkIO.set("MatchData", "SHIFT", currentShift);
  }

  @Override
  public void disabledInit() {
    LimelightHelpers.SetIMUMode(limelightName, 1);
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      CommandScheduler.getInstance().schedule(m_autonomousCommand);
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();

    CommandScheduler.getInstance()
        .schedule(TestScheduler.runTest(m_robotContainer.getTestRoutine()));
  }

  @Override
  public void testPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void testExit() {}
}
