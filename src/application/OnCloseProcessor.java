package application;

public abstract class OnCloseProcessor {
    OnCloseProcessor nextProcessor;
    OnCloseProcessor(OnCloseProcessor processor) {
        this.nextProcessor = processor;
    }
    public void process(Auction auction) {
        if (nextProcessor != null) {
            nextProcessor.process(auction);
        }
    }

}
