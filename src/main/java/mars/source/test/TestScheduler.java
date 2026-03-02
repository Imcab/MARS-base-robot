package mars.source.test;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import mars.source.utils.TerminalBooter;

public class TestScheduler {
    
    public static Command runTest(TestRoutine test) {
        
        // 1. Escudo Anti-Null
        if (test == null) {
            TerminalBooter.logError("Tests", "TestRoutine is null! Ignorando ejecución.");
            return Commands.none(); // Devuelve un comando vacío e inofensivo
        }

        // 2. Extraer el nombre de tu anotación
        String testName = test.getClass().getSimpleName();
        if (test.getClass().isAnnotationPresent(MARSTest.class)) {
            testName = test.getClass().getAnnotation(MARSTest.class).name();
        }
        final String finalName = testName;

        // 3. Tomar el comando nativo y "decorarlo" con la telemetría de MARS
        return test.getRoutineCommand()
            .beforeStarting(() -> {
                System.out.println("Starting test: " + finalName);
                TerminalBooter.logInfo("Tests", "RUNNING: " + finalName);
            })
            .finallyDo((interrupted) -> {
                if (interrupted) {
                    // Si algo (o alguien) cancela el comando a la mitad
                    TerminalBooter.logWarning("Tests", "ABORTADO: " + finalName);
                } else {
                    // Si el comando terminó toda su secuencia
                    TerminalBooter.logInfo("Tests", "FINALIZADO: " + finalName);
                }
            })
            .withName("MARS-Test-" + finalName) // Nombre para el panel de WPILib
            .ignoringDisable(true); // Permite que corra en Test Mode o Disabled
    }
}