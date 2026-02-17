package frc.robot.core.modules.swerve.nodes;

import java.util.function.Consumer;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import frc.robot.configuration.Manifest;
import frc.robot.core.modules.swerve.nodes.drivers.QuestNavDriver;
import frc.robot.core.modules.swerve.nodes.drivers.SimDriver;

public class QuestNavNode extends VisionNode{

    public interface QuestNavIO extends VisionIO{
        void loadPose(Pose3d pose3d);    
    }

    public QuestNavNode(
            String name, Transform3d robotToQuest, Consumer<VisionMsg> topicPublisher) {
        
        super(
            name, 
            Manifest.CURRENT_MODE == Manifest.Mode.REAL 
                ? new QuestNavDriver(robotToQuest) 
                : new SimDriver(), 
            topicPublisher
        );
    }

    public void resetQuestPose(Pose3d pose){
        if(this.hardwareDriver instanceof QuestNavDriver){
            QuestNavIO excusiveDriver = (QuestNavIO) this.hardwareDriver;

            excusiveDriver.loadPose(pose);
        }
    }
}
