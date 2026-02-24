package mars.source.services.nodes;

public class FallbackNode<M extends NodeMessage<M>> extends Node<M> {
    
    public FallbackNode() {
        super("FallbackNode", null, null);
    }

    @Override
    public boolean isFallback() {
        return true; 
    }

    @Override
    protected void processInformation() {
    }
}