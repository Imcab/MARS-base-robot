package mars.source.models.nodes;

import mars.source.models.singlemodule.Data;

public abstract class NodeMessage<M> extends Data<M> {

    public abstract void telemeterize(String tableName);

}