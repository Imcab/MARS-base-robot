package mars.source.test;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import mars.source.utils.TerminalBooter;

public class TestScheduler {
    
    public static Command runTest(TestRoutine test) {
        
        if (test == null) {
            TerminalBooter.logError("Tests", "TestRoutine is null!");
            return Commands.none();
        }

        String testName = test.getClass().getSimpleName();
        if (test.getClass().isAnnotationPresent(MARSTest.class)) {
            testName = test.getClass().getAnnotation(MARSTest.class).name();
        }
        final String finalName = testName;

        return test.getRoutineCommand()
            .beforeStarting(() -> {
                System.out.println("Starting test: " + finalName);
                TerminalBooter.logInfo("Tests", "RUNNING: " + finalName);
            })
            .finallyDo((interrupted) -> {
                if (interrupted) {

                    TerminalBooter.logWarning("Tests", "INTERRUPTED: " + finalName);
                } else {
                    // Si el comando terminó toda su secuencia
                    TerminalBooter.logInfo("Tests", "FINALIZED: " + finalName);
                }
            })
            .withName("MARS-Test-" + finalName)
            .ignoringDisable(true);
    }
}