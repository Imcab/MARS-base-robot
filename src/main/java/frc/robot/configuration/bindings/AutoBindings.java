package frc.robot.configuration.bindings;

import com.stzteam.forgemini.io.SmartChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.Manifest.AutoBuilder;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.modules.swerve.visionNode.VisionNode.VisionMsg;
import mars.source.models.containers.Binding;
import mars.source.services.nodes.Node;


public class AutoBindings implements Binding {

    private final SmartChooser<Command> chooser;
    private CommandSwerveDrivetrain drivetrain;
    private Node<VisionMsg> questnav;

    private AutoBindings(SmartChooser<Command> chooser) {
        this.chooser = chooser;
    }

    public static AutoBindings create(SmartChooser<Command> chooser) {
        return new AutoBindings(chooser);
    }

    public AutoBindings withDrivetrain(CommandSwerveDrivetrain drivetrain) {
        this.drivetrain = drivetrain;
        return this;
    }

    public AutoBindings withNodes(Node<VisionMsg> questnav) {
        this.questnav = questnav;
        return this;
    }

    @Override
    public void bind() {
        this.chooser.setDefault("Do Nothing", Commands.none())
            .add("Move Hub", AutoBuilder.buildPath("New Auto", drivetrain, questnav))
            .add("Rotar", AutoBuilder.buildPath("Rotacion", drivetrain, questnav))
            .add("Cuadrado", AutoBuilder.buildPath("Square", drivetrain, questnav))
            .add("Bump 2 Loop", AutoBuilder.buildPath("Bump2Loop", drivetrain, questnav))
            .add("Sim Test", AutoBuilder.buildPath("SimTest", drivetrain, questnav));

        // Publicamos a la Shuffleboard/SmartDashboard
        this.chooser.publish();
    }
}