// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.diagnostics;

import com.stzteam.mars.diagnostics.DiagnosticPattern;
import com.stzteam.mars.diagnostics.StatusCode;
import edu.wpi.first.wpilibj.util.Color;

public enum ClimberCode implements StatusCode {
  IDLE(Severity.OK, DiagnosticPattern.solid(Color.kDarkGreen)),

  CLIMBING(Severity.WARNING, DiagnosticPattern.solid(Color.kYellow)),
  VOLTAGE(Severity.OK, DiagnosticPattern.solid(Color.kFirstBlue)),

  DOWN(Severity.WARNING, DiagnosticPattern.solid(Color.kOrange));

  private final Severity severity;
  private final DiagnosticPattern pattern;

  ClimberCode(Severity severity, DiagnosticPattern pattern) {
    this.severity = severity;
    this.pattern = pattern;
  }

  @Override
  public Severity getSeverity() {
    return this.severity;
  }

  @Override
  public String getName() {
    return this.name();
  }

  @Override
  public DiagnosticPattern getVisualPattern() {
    return this.pattern;
  }
}
