package mars.source.operator;

import mars.source.operator.ControllerContainer.ActionButtons;
import mars.source.operator.ControllerContainer.AnalogAxis;
import mars.source.operator.ControllerContainer.AnalogTriggers;
import mars.source.operator.ControllerContainer.Bumpers;
import mars.source.operator.ControllerContainer.DPadTriggers;
import mars.source.operator.ControllerContainer.SpecialTriggers;
import mars.source.operator.ControllerContainer.Stick;

public interface ControllerOI {

    public Stick getLeftStick();
    public Stick getRightStick();

    public Bumpers getBumpers();
    public DPadTriggers getDPadTriggers();
    public SpecialTriggers getSystemTriggers();
    public ActionButtons getActionButtons();
    public AnalogTriggers getAnalogTriggers();
    public AnalogAxis getAnalogAxis();

}
