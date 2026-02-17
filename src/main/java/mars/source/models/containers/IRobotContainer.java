package mars.source.models.containers;

import edu.wpi.first.wpilibj2.command.Command;

public interface IRobotContainer {
    void updateNodes();
    Command getAutonomousCommand();
}
