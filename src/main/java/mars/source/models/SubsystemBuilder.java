package mars.source.models;

import mars.source.models.singlemodule.Data;
import mars.source.models.singlemodule.IO;
import mars.source.requests.Request;

public class SubsystemBuilder<D extends Data<D>, A extends IO<D>> {
    private String key;
    private D inputs;
    private A actor;
    private Request<D, A> initialRequest;
    private Telemetry<D> telemetry;

    private SubsystemBuilder() {}

    public static <D extends Data<D>, A extends IO<D>> SubsystemBuilder<D, A> setup() {
        return new SubsystemBuilder<>();
    }

    public SubsystemBuilder<D, A> key(String key) { 
        this.key = key; 
        return this; 
    }

    public SubsystemBuilder<D, A> hardware(A actor, D inputs) { 
        this.actor = actor; 
        this.inputs = inputs; 
        return this; 
    }

    public SubsystemBuilder<D, A> request(Request<D, A> req) { 
        this.initialRequest = req; 
        return this; 
    }

    public SubsystemBuilder<D, A> telemetry(Telemetry<D> tel) { 
        this.telemetry = tel; 
        return this; 
    }

    // --- Getters para el ModularSubsystem ---
    public String getKey() { return key; }
    public D getInputs() { return inputs; }
    public A getActor() { return actor; }
    public Request<D, A> getInitialRequest() { return initialRequest; }
    public Telemetry<D> getTelemetry() { return telemetry; }
}