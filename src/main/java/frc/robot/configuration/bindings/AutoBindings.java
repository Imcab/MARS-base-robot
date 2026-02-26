package frc.robot.configuration.bindings;

import com.pathplanner.lib.auto.NamedCommands;
import com.stzteam.forgemini.io.SmartChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.Manifest.AutoBuilder;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.modules.swerve.visionNode.VisionNode.VisionMsg;
import mars.source.models.containers.Binding;
import mars.source.services.nodes.Node;


public class AutoBindings implements Binding {

    private final SmartChooser<Command> chooser;
    private CommandSwerveDrivetrain drivetrain;
    private final Superstructure superstructure;
    private Node<VisionMsg> questnav;

    private AutoBindings(SmartChooser<Command> chooser, Superstructure superstructure) {
        this.chooser = chooser;
        this.superstructure = superstructure;
    }

    public static AutoBindings create(SmartChooser<Command> chooser, Superstructure superstructure) {
        return new AutoBindings(chooser, superstructure);
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
            .add("EatAuto1", AutoBuilder.buildPath("Eat1", drivetrain, questnav));

        NamedCommands.registerCommand("Angle->Eat", superstructure.EatAutoAngle(140, 4, intakeMODE.kDOWN, -10));
        NamedCommands.registerCommand("Eat", superstructure.EatAutoWheels(-10));
        
        // Publicamos a la Shuffleboard/SmartDashboard
        this.chooser.publish();
    }
}