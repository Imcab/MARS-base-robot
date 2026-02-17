package mars.source.models.singlemodule;

import edu.wpi.first.wpilibj.Timer;

public class Data<T>{
    public double timestamp = Timer.getFPGATimestamp();;
    public String key;

    @SuppressWarnings("unchecked")
    public T snapshot() {
        return (T) this;
    }
}
