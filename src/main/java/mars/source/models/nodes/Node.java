package mars.source.models.nodes;

import java.util.function.Consumer;

public abstract class Node<M extends NodeMessage<M>> {
    
    protected final String nodeName;
    protected final M messagePayload; 
    protected final Consumer<M> topicPublisher; 

    public Node(String nodeName, M emptyMessage, Consumer<M> topicPublisher) {
        this.nodeName = nodeName;
        this.messagePayload = emptyMessage;
        this.topicPublisher = topicPublisher;
    }

    public void periodic() {

        updateHardware(); 
    
        messagePayload.telemeterize(nodeName);

        if (topicPublisher != null) {
            topicPublisher.accept(messagePayload);
        }
    }

    protected abstract void updateHardware();
}