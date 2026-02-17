package mars.source.models;

import mars.source.diagnostics.ActionStatus;
import mars.source.models.singlemodule.Data;

public abstract class Telemetry<D extends Data<D>> {
    
    public abstract void telemeterize(D data, ActionStatus lastStatus);
}
