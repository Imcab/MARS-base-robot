package frc.robot.core.modules.swerve.visionNode.limelight;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.configuration.Manifest;
import frc.robot.core.modules.swerve.visionNode.VisionNode;
import frc.robot.core.modules.swerve.visionNode.VisionSimDriver;
import frc.robot.core.modules.swerve.visionNode.VisionNode.VisionMsg;
import mars.source.builder.Injector;

public class LimelightNode extends VisionNode {

    public LimelightNode(
            String name, 
            Supplier<Rotation2d> yawSupplier, 
            DoubleSupplier yawRateSupplier, 
            Consumer<VisionMsg> topicPublisher) {
        
        super(
            name,
            Injector.createIO(
                Manifest.HAS_LIMELIGHT,
                VisionSimDriver::new,
                () -> new LimelightDriver(name, yawSupplier, yawRateSupplier),
                VisionSimDriver::new

            ),
            topicPublisher
        );
    }
}
