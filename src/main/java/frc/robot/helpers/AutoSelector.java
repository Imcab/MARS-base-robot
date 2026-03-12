// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.helpers;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import java.util.List;

public class AutoSelector {

  private static final int MAX_QUESTIONS = 4;
  private static final AutoRoutine DEFAULT_ROUTINE =
      new AutoRoutine("Nothing", List.of(), Commands.none());

  private final String baseKey;
  private final SendableChooser<AutoRoutine> routineChooser;
  private final NetworkTable table;
  private AutoRoutine lastRoutine;

  private boolean isLocked = false;

  public AutoSelector(String key) {
    this.baseKey = key;
    routineChooser = new SendableChooser<>();
    routineChooser.setDefaultOption(DEFAULT_ROUTINE.name(), DEFAULT_ROUTINE);
    SmartDashboard.putData(key + "/Routine", routineChooser);

    lastRoutine = DEFAULT_ROUTINE;
    table = NetworkTableInstance.getDefault().getTable("SmartDashboard/" + key);
  }

  public void addRoutine(String name, Command command) {
    addRoutine(name, List.of(), command);
  }

  public void addRoutine(String name, List<AutoQuestion> questions, Command command) {
    if (questions.size() > MAX_QUESTIONS) {
      throw new RuntimeException(
          "Auto " + name + " exceeds the limit of max questions: " + MAX_QUESTIONS);
    }

    routineChooser.addOption(name, new AutoRoutine(name, questions, command));
  }

  public Command getCommand() {
    return lastRoutine.command();
  }

  public boolean getBooleanResponse(int questionIndex) {
    return table.getEntry("/Question_" + (questionIndex + 1) + "_Response").getBoolean(false);
  }

  public double getDoubleResponse(int questionIndex) {
    return table.getEntry("/Question_" + (questionIndex + 1) + "_Response").getDouble(0.0);
  }

  @SuppressWarnings("unchecked")
  public <V> V getChooserResponse(int questionIndex) {
    if (questionIndex < lastRoutine.questions().size()) {
      AutoQuestion q = lastRoutine.questions().get(questionIndex);
      if (q.dataType() == DataType.CHOICE && q.chooser() != null) {
        return (V) q.chooser().getSelected();
      }
    }
    return null;
  }

  public void lockSelection() {
    this.isLocked = true;
    System.out.println("Locked");
  }

  public void unlockSelection() {
    this.isLocked = false;
    System.out.println("Unlocked");
  }

  public void periodic() {
    if (DriverStation.isAutonomousEnabled()) return;

    if (!DriverStation.isDisabled() || isLocked) {
      return;
    }

    AutoRoutine selectedRoutine = routineChooser.getSelected();
    if (selectedRoutine == null || selectedRoutine.name().equals(lastRoutine.name())) {
      return;
    }

    List<AutoQuestion> questions = selectedRoutine.questions();

    for (int i = 0; i < MAX_QUESTIONS; i++) {
      String qKey = "/Question_" + (i + 1);

      if (i < questions.size()) {
        AutoQuestion q = questions.get(i);

        table.getEntry(qKey + "_Label").setString(q.label());

        if (q.dataType() == DataType.CHOICE) {

          SmartDashboard.putData(baseKey + qKey + "_Chooser", q.chooser());
        } else {
          NetworkTableEntry responseEntry = table.getEntry(qKey + "_Response");
          if (q.dataType() == DataType.BOOLEAN) responseEntry.setBoolean(false);
          else if (q.dataType() == DataType.DOUBLE) responseEntry.setDouble(0.0);
        }

      } else {

      }
    }
    lastRoutine = selectedRoutine;
  }

  private record AutoRoutine(String name, List<AutoQuestion> questions, Command command) {}

  private enum DataType {
    CHOICE,
    BOOLEAN,
    DOUBLE
  }

  public record AutoQuestion(String label, DataType dataType, SendableChooser<?> chooser) {

    public static <V> AutoQuestion choice(String label, SendableChooser<V> chooser) {
      return new AutoQuestion(label, DataType.CHOICE, chooser);
    }

    public static AutoQuestion toggle(String label) {
      return new AutoQuestion(label, DataType.BOOLEAN, null);
    }

    public static AutoQuestion slider(String label) {
      return new AutoQuestion(label, DataType.DOUBLE, null);
    }
  }
}
