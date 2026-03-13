// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.bindings;

import com.stzteam.mars.models.containers.Binding;
import com.stzteam.mars.test.TestRoutine;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.tests.ArmTest;
import frc.tests.IndexerTest;
import frc.tests.IntakeTest;
import frc.tests.IntakeWheelsTest;
import frc.tests.InterpolateTest;
import frc.tests.ShooterRMPtest;
import frc.tests.ShooterWheelsVoltage;
import frc.tests.TurretTest;

public class TestBindings implements Binding {

  private SendableChooser<TestRoutine> tests = new SendableChooser<>();

  private Superstructure s;

  private TestBindings(Superstructure sup) {
    this.s = sup;
  }

  public static TestBindings create(Superstructure sup) {
    return new TestBindings(sup);
  }

  @Override
  public void bind() {

    tests.addOption("IntakeTest", new IntakeTest(s.getIntake()));
    tests.addOption("TurretTest", new TurretTest(s.getTurret()));
    tests.addOption("ArmTest", new ArmTest(s.getArm()));
    tests.addOption("IndexTest", new IndexerTest(s.getIndexer()));
    tests.addOption("InterTest", new InterpolateTest(s));
    tests.addOption("IntakeWheels Test", new IntakeWheelsTest(s.getFlyWheelsIntake()));
    tests.addOption("ShooterVoltage Test", new ShooterWheelsVoltage(s.getFlywheelShooter()));
    tests.addOption("ShooterRMP Test", new ShooterRMPtest(s.getFlywheelShooter()));

    SmartDashboard.putData("TestRoutines", tests);
  }

  public TestRoutine getSelected() {
    return tests.getSelected();
  }
}
