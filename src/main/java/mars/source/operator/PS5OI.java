package mars.source.operator;

import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import mars.source.operator.ControllerContainer.ActionButtons;
import mars.source.operator.ControllerContainer.AnalogAxis;
import mars.source.operator.ControllerContainer.AnalogTriggers;
import mars.source.operator.ControllerContainer.Bumpers;
import mars.source.operator.ControllerContainer.DPadTriggers;
import mars.source.operator.ControllerContainer.SpecialTriggers;
import mars.source.operator.ControllerContainer.Stick;

public class PS5OI implements ControllerOI{

    private final CommandPS5Controller controller;

    private final Stick leftStick;
    private final Stick rightStick;
    private final ActionButtons buttons;
    private final Bumpers bumpers;
    private final DPadTriggers dPad;
    private final SpecialTriggers system;
    private final AnalogTriggers analog;
    private final AnalogAxis analogAxis;
    
    public PS5OI(int port){
        this.controller = new CommandPS5Controller(port);

        this.leftStick = new Stick(controller::getLeftX, controller::getLeftY);
        this.rightStick = new Stick(controller::getRightX, controller::getRightY);

        this.buttons = new ActionButtons(controller.cross(), controller.circle(), controller.square(), controller.triangle());
        this.bumpers = new Bumpers(controller.L1(), controller.R1());
        this.dPad = new DPadTriggers(controller.povUp(), controller.povDown(), controller.povLeft(), controller.povRight());
        this.system = new SpecialTriggers(controller.create(), controller.options());
        this.analog = new AnalogTriggers(controller.L2(), controller.R2());
        this.analogAxis = new AnalogAxis(controller::getL2Axis, controller::getR2Axis);

    }

   @Override
    public Stick getLeftStick() {
        return leftStick;
    }

    @Override
    public Stick getRightStick() {
        return rightStick;
    }

    @Override
    public Bumpers getBumpers() {
        return bumpers;
    }

    @Override
    public DPadTriggers getDPadTriggers() {
        return dPad;
    }

    @Override
    public SpecialTriggers getSystemTriggers() {
        return system;
    }

    @Override
    public ActionButtons getActionButtons() {
        return buttons;
    }

    @Override
    public AnalogTriggers getAnalogTriggers() {
        return analog;
    }

    @Override
    public AnalogAxis getAnalogAxis() {
        return analogAxis;
    }
    
}
