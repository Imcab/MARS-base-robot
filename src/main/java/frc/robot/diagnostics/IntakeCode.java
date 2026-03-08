// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.diagnostics;

import com.stzteam.mars.diagnostics.DiagnosticPattern;
import com.stzteam.mars.diagnostics.StatusCode;
import edu.wpi.first.wpilibj.util.Color;

public enum IntakeCode implements StatusCode {
  IDLE(Severity.OK, DiagnosticPattern.breathing(Color.kDarkGreen)),
  ON_TARGET(Severity.OK, DiagnosticPattern.solid(Color.kFirstBlue)),

  MOVING_TO_ANGLE(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kYellow)),
  MANUAL_OVERRIDE(Severity.WARNING, DiagnosticPattern.blinkSlow(Color.kPurple)),

  RESET(Severity.OK, DiagnosticPattern.solid(Color.kDarkSalmon)),

  OUT_OF_RANGE(Severity.ERROR, DiagnosticPattern.blinkFast(Color.kOrange));

  private final Severity severity;
  private final DiagnosticPattern pattern;

  IntakeCode(Severity severity, DiagnosticPattern pattern) {
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
