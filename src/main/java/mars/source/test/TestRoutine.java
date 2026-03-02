package mars.source.test;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import mars.source.utils.TerminalBooter;

public abstract class TestRoutine {

    public abstract Command getRoutineCommand();

    protected Command waitFor(BooleanSupplier condition, double timeoutSeconds) {
        return Commands.waitUntil(condition).withTimeout(timeoutSeconds);
    }

    protected Command delay(double seconds) {
        return Commands.waitSeconds(seconds);
    }

    protected Command assertLessThan(DoubleSupplier valueSupplier, double max, String message) {
        return Commands.runOnce(() -> {
            double value = valueSupplier.getAsDouble();
            if (value >= max) {
                TerminalBooter.logError("Tests", "FAIL: " + message + " | Valor: " + value + " >= " + max);
                // Opcional: throw new MARSAssertionError(...) si quieres detener el robot por completo
            } else {
                TerminalBooter.logInfo("Tests", "PASS: Check < " + max);
            }
        });
    }

    protected Command assertGreaterThan(DoubleSupplier valueSupplier, double min, String message) {
        return Commands.runOnce(() -> {
            double value = valueSupplier.getAsDouble();
            if (value <= min) {
                TerminalBooter.logError("Tests", "FAIL: " + message + " | Valor: " + value + " <= " + min);
            } else {
                TerminalBooter.logInfo("Tests", "PASS: Check > " + min);
            }
        });
    }

    protected Command assertTrue(BooleanSupplier condition, String message) {
        return Commands.runOnce(() -> {
            if (!condition.getAsBoolean()) {
                TerminalBooter.logError("Tests", "FAIL: " + message);
            } else {
                TerminalBooter.logInfo("Tests", "PASS: Condición cumplida.");
            }
        });
    }
}