package mars.source.requests;

import mars.source.diagnostics.ActionStatus;

@FunctionalInterface
public interface Request<P,A>{

    public ActionStatus apply(P parameters, A actor);

}
