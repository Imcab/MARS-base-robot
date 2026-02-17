package mars.source.operator;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.button.Trigger;

public class ControllerContainer {

    public static record Stick(DoubleSupplier x, DoubleSupplier y) {}

    // Movimiento direccional
    public static record DPadTriggers(Trigger up, Trigger down, Trigger left, Trigger right) {}

    // Botones superiores
    public static record Bumpers(Trigger left, Trigger right) {}

    // Botones frontales (A,B,X,Y en Xbox | Cruz, Círculo, Cuadrado, Triángulo en PS5)
    public record ActionButtons(Trigger bottom, Trigger right, Trigger left, Trigger top) {}
    
    // Botones de sistema
    public static record SpecialTriggers(Trigger back, Trigger start){}

    // Gatillos traseros (L2, R2 / LT, RT) convertidos a botones digitales
    public record AnalogTriggers(Trigger left, Trigger right) {}

    public record AnalogAxis(DoubleSupplier left, DoubleSupplier rigth) {}

}
