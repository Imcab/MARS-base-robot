package frc.robot.configuration.bindings;

import com.stzteam.forgemini.io.SmartChooser;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configuration.Manifest.AutoBuilder;
import frc.robot.core.modules.swerve.CommandSwerveDrivetrain;
import frc.robot.core.modules.swerve.nodes.QuestNavNode;
import mars.source.models.containers.Binding;

public class AutoBindings implements Binding{

    private final SmartChooser<Command> chooser;
    private final CommandSwerveDrivetrain drivetrain;
    private final QuestNavNode questnav;

    private AutoBindings(SmartChooser<Command> chooser, CommandSwerveDrivetrain drivetrain, QuestNavNode questnav){
        this.chooser = chooser;
        this.drivetrain = drivetrain;
        this.questnav = questnav;
    }

    public static AutoBindings parameterized(SmartChooser<Command> chooser, CommandSwerveDrivetrain drivetrain, QuestNavNode questnav){
        return new AutoBindings(chooser, drivetrain, questnav);
    }

    @Override
    public void bind() {

        this.chooser.setDefault("Do Nothing", Commands.none())
            
        .add("Move Hub", AutoBuilder.buildPath("New Auto", drivetrain, questnav))
        .add("Rotar", AutoBuilder.buildPath("Rotacion", drivetrain, questnav))
        .add("Cuadrado", AutoBuilder.buildPath("Square", drivetrain, questnav))
        .add("Bump 2 Loop", AutoBuilder.buildPath("Bump2Loop", drivetrain, questnav))
        .add("Sim Test", AutoBuilder.buildPath("SimTest", drivetrain, questnav));

        this.chooser.publish();
        
    }
    
}
