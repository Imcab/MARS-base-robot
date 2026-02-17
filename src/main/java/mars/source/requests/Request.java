package mars.source.requests;

import mars.source.diagnostics.ActionStatus;
import mars.source.utils.Empty;

@FunctionalInterface
public interface Request<P,A>{

    public ActionStatus apply(P parameters, A actor);

    public interface  ParameterlessRequest<A> extends Request<Empty, A>{
        public ActionStatus apply(A actor);

        @Override
        public default ActionStatus apply(Empty params, A actor) {
            return apply(actor);
        }
        
    }

    public interface ActorlessRequest<P> extends Request<P, Empty> {
        ActionStatus apply(P parameters);

        @Override
        default ActionStatus apply(P parameters, Empty actor) {
            return apply(parameters);
        }
    }
    
}
