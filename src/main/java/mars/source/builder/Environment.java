package mars.source.builder;

public class Environment {
    
    private static RunMode currentMode = RunMode.REAL;

    public static void setMode(RunMode mode) {
        currentMode = mode;
    }

    public static RunMode getMode() {
        return currentMode;
    }
    
}
