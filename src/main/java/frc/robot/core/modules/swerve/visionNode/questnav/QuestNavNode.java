package frc.robot.core.modules.swerve.visionNode.questnav;

import java.util.function.Consumer;
import java.util.function.Supplier;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import frc.robot.configuration.Manifest;
import frc.robot.core.modules.swerve.visionNode.VisionNode;
import frc.robot.core.modules.swerve.visionNode.VisionSimDriver;
import mars.source.builder.Injector;

public class QuestNavNode extends VisionNode {


    public interface QuestNavIO extends VisionIO {
        void loadPose(Pose3d pose3d);
    }

    public QuestNavNode(
            String name, 
            Transform3d robotToQuest, 
            Consumer<VisionMsg> topicPublisher) {
        
        super(
            name, 
            Injector.<VisionIO>createIO(
                Manifest.HAS_QUESTNAV,
                (Supplier<VisionIO>) VisionSimDriver::new,
                () -> new QuestNavDriver(robotToQuest),
                (Supplier<VisionIO>) VisionSimDriver::new
            ), 
            topicPublisher
        );
    }

    public void resetQuestPose(Pose3d pose) {
        if (this.hardwareDriver instanceof QuestNavIO exclusiveDriver) {
            exclusiveDriver.loadPose(pose);
        }
    }
}
