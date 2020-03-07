package application.processor;

abstract class AuctionCloseNotifier extends OnCloseProcessor {
    public AuctionCloseNotifier(OnCloseProcessor processor) {
        super(processor);
    }
}
