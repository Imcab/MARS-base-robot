package mars.source.models.containers;

import mars.source.operator.ControllerOI;

@FunctionalInterface
public interface Binding {
    void bind(ControllerOI controller);
}
