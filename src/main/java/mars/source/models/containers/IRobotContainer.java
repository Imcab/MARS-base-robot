package mars.source.models.containers;

import edu.wpi.first.wpilibj2.command.Command;
import mars.source.test.TestRoutine;

public interface IRobotContainer {
    void updateNodes();
    Command getAutonomousCommand();
    TestRoutine getTestRoutine();
}
