package frc.robot.core.modules.swerve.nodes.drivers;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import frc.robot.core.modules.swerve.nodes.QuestNavNode.QuestNavIO;
import frc.robot.core.modules.swerve.nodes.VisionNode.VisionMsg;
import gg.questnav.questnav.PoseFrame;
import gg.questnav.questnav.QuestNav;

public class QuestNavDriver implements QuestNavIO{

    private final QuestNav quest;
    private final Transform3d robotToQuest;

    public QuestNavDriver(Transform3d robotToQuest){
        this.robotToQuest = robotToQuest;
        this.quest = new QuestNav();

    }

    @Override
    public void updateData(VisionMsg data) {

        quest.commandPeriodic();
        data.hasTarget = quest.isTracking() && quest.isConnected();

         PoseFrame[] questFrames = quest.getAllUnreadPoseFrames();

         for (PoseFrame questFrame : questFrames) {
            
            if (questFrame.isTracking()) {
               
                Pose3d questPose = questFrame.questPose3d();
             
                Pose3d robotPose = questPose.transformBy(robotToQuest.inverse());

                data.timestamp = questFrame.dataTimestamp();
                data.botPose = robotPose.toPose2d();
                
                
            }
        }

    }

    @Override
    public void loadPose(Pose3d pose){
        Pose3d questPose = pose.transformBy(robotToQuest.inverse());
        quest.setPose(questPose);
    }
    
}
