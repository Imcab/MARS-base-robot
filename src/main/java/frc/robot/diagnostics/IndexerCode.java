// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.diagnostics;

import com.stzteam.mars.diagnostics.DiagnosticPattern;
import com.stzteam.mars.diagnostics.StatusCode;
import edu.wpi.first.wpilibj.util.Color;

public enum IndexerCode implements StatusCode {
  IDLE(Severity.OK, DiagnosticPattern.solid(Color.kDarkGreen)),
  VOLTAGE(Severity.OK, DiagnosticPattern.solid(Color.kFirstBlue)),
  SPEED(Severity.OK, DiagnosticPattern.solid(Color.kYellow)),

  STOPED(Severity.WARNING, DiagnosticPattern.solid(Color.kRed));

  private final Severity severity;
  private final DiagnosticPattern pattern;

  IndexerCode(Severity severity, DiagnosticPattern pattern) {
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
