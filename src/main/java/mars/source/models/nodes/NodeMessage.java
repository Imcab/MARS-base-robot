package mars.source.models.nodes;

import mars.source.models.singlemodule.Data;

/**
 * Base para cualquier mensaje que viaje por la red de Nodos (ROS Style).
 * Sabe cómo auto-publicarse en NetworkTables.
 */
public abstract class NodeMessage<M> extends Data<M> {
    
    /**
     * Publica el estado interno del mensaje a NetworkTables.
     * @param tableName La tabla principal
     */
    public abstract void telemeterize(String tableName);

}