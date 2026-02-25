package mars.source.builder;

import java.util.function.Supplier;

public class Injector {

    public static <T> T createIO(
            boolean isEnabled, 
            Supplier<T> fallbackSupplier, 
            Supplier<T> realSupplier, 
            Supplier<T> simSupplier) {
        
        if (!isEnabled) {
            return fallbackSupplier.get();
        }

        switch (Environment.getMode()) {
            case REAL: 
                return realSupplier.get();
            case SIM:
            case REPLAY:
            default:   
                return simSupplier.get();
        }
    }
}