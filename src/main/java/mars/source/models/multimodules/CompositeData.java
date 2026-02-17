package mars.source.models.multimodules;

import mars.source.models.singlemodule.Data;

public abstract class CompositeData<T extends CompositeData<T>> extends Data<T> {

}