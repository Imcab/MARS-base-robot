package frc.robot.core.modules.swerve.visionNode;

import mars.source.services.Query;

public class VisionQuery implements Query {

    public final int targetId; 
    public VisionQuery(int targetId) { this.targetId = targetId; }
    public VisionQuery() { this(0); }
}
