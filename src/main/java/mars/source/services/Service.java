package mars.source.services;

@FunctionalInterface
public interface Service<Q extends Query, R extends Reply> {
    R execute(Q query);
}
