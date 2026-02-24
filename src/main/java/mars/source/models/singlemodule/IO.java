package mars.source.models.singlemodule;

@FunctionalInterface
public interface IO<T extends Data<T>> {


    void updateInputs(T inputs);

    default boolean isFallback() {
        return false;
    }
}