// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.bindings;

import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.stzteam.mars.models.containers.Binding;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;
import frc.robot.core.requests.moduleRequests.IntakeRequestFactory;

public class AutoBindings implements Binding {

  private final Superstructure superstructure;

  public SendableChooser<Command> chooser = new SendableChooser<>();
  public PathPlannerAuto AutoCenter;
  public PathPlannerAuto autoForeward;
  public PathPlannerAuto bumpPost;
  public PathPlannerAuto depotAuto;

  private AutoBindings(Superstructure s) {
    this.superstructure = s;
  }

  public static AutoBindings create(Superstructure s) {
    return new AutoBindings(s);
  }

  private void registerAutoCommands() {
    NamedCommands.registerCommand(
        "Angle->Eat_4.5",
        superstructure.EatAutoAngle(-140, 4, intakeMODE.kDOWN, -10).withTimeout(4.5));

    NamedCommands.registerCommand("Eat_5", superstructure.EatAutoWheels(-10).withTimeout(5));
    NamedCommands.registerCommand("Eat_3.5", superstructure.EatAutoWheels(-10).withTimeout(3.5));

    NamedCommands.registerCommand("Shoot_6", superstructure.shootAuto().withTimeout(6));
    NamedCommands.registerCommand("Shoot_10", superstructure.shootAuto().withTimeout(10));
    NamedCommands.registerCommand("Shoot_13", superstructure.shootAuto().withTimeout(12));

    NamedCommands.registerCommand(
        "IntakeUp",
        superstructure
            .getIntake()
            .setControl(() -> IntakeRequestFactory.setAngle().withAngle(-10).Tolerance(2)));
  }

  @Override
  public void bind() {
    registerAutoCommands();

    AutoCenter = new PathPlannerAuto("AutoCenter");
    autoForeward = new PathPlannerAuto("New Auto");
    bumpPost = new PathPlannerAuto("EatPost-auto");
    depotAuto = new PathPlannerAuto("DepotAuto");

    chooser.setDefaultOption("Post", bumpPost);
    chooser.addOption("AutoCenter", AutoCenter);
    chooser.addOption("test1Forward", autoForeward);

    SmartDashboard.putData("AutoSelector", chooser);
  }

  public Command get() {
    return chooser.getSelected();
  }
}
