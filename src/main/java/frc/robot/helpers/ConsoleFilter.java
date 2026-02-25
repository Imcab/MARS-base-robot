package frc.robot.helpers;

import java.io.PrintStream;

public class ConsoleFilter extends PrintStream {
    private final PrintStream originalStream;
    
    private static final String[] SPAM_PHRASES = {                                                                                  
        "NT: server: client connection timed out",
        "Default simulationPeriodic() method... Override me!",
    };

    public ConsoleFilter(PrintStream originalStream) {
        super(originalStream);
        this.originalStream = originalStream;
    }

    @Override
    public void println(String x) {
        if (isSpam(x)) return; 
        originalStream.println(x); 
    }

    @Override
    public void print(String x) {
        if (isSpam(x)) return;
        originalStream.print(x);
    }

    private boolean isSpam(String message) {
        if (message == null) return false;

        for (String spam : SPAM_PHRASES) {
            if (message.contains(spam)) {
                return true;
            }
        }
        
        return false;
    }
}