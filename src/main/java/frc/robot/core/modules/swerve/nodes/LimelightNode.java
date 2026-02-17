package frc.robot.core.modules.swerve.nodes;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.configuration.Manifest;
import frc.robot.core.modules.swerve.nodes.drivers.LimelightDriver;
import frc.robot.core.modules.swerve.nodes.drivers.SimDriver;

public class LimelightNode extends VisionNode {

    public LimelightNode(
            String name, 
            Supplier<Rotation2d> yawSupplier, 
            DoubleSupplier yawRateSupplier, 
            Consumer<VisionMsg> topicPublisher) {
        
        super(
            name, 
            Manifest.CURRENT_MODE == Manifest.Mode.REAL 
                ? new LimelightDriver(name, yawSupplier, yawRateSupplier) 
                : new SimDriver(), 
            topicPublisher
        );
    }

    
}