package frc.robot.core.modules.swerve.visionNode.questnav;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import frc.robot.core.modules.swerve.visionNode.VisionNode.VisionMsg;
import frc.robot.core.modules.swerve.visionNode.questnav.QuestNavNode.QuestNavIO;
import gg.questnav.questnav.PoseFrame;
import gg.questnav.questnav.QuestNav;

public class QuestNavDriver implements QuestNavIO {

    private final QuestNav quest;
    private final Transform3d robotToQuest;

    public QuestNavDriver(Transform3d robotToQuest) {
        this.robotToQuest = robotToQuest;
        this.quest = new QuestNav();
    }

    @Override
    public void updateData(VisionMsg data) {
        quest.commandPeriodic();
        
        data.hasTarget = quest.isConnected() && quest.isTracking();

        PoseFrame[] questFrames = quest.getAllUnreadPoseFrames();

        if (questFrames.length == 0) {
            data.validPose = false;
            return;
        }

        for (PoseFrame questFrame : questFrames) {
            if (questFrame.isTracking()) {
                Pose3d questPose = questFrame.questPose3d();
                
                Pose3d robotPose = questPose.transformBy(robotToQuest.inverse());

                data.timestamp = questFrame.dataTimestamp();
                data.botPose = robotPose.toPose2d();
                data.validPose = true;
                
            }
        }
    }

    @Override
    public void loadPose(Pose3d pose) {
        Pose3d questPose = pose.transformBy(robotToQuest);
        quest.setPose(questPose);
    }
}