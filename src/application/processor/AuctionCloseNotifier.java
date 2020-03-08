package application.processor;

import com.tobeagile.training.ebaby.services.PostOffice;

import application.Auction;

class AuctionCloseNotifier extends OnCloseProcessor {
    public AuctionCloseNotifier(OnCloseProcessor processor) {
        super(processor);
    }

    @Override
    public void process(Auction auction) {
        if (isTargetTransaction(auction)) {
            PostOffice postOffice = PostOffice.getInstance();
            postOffice.sendEMail(getSendTo(auction), getMessage(auction));
        }
        super.process(auction);
    }

    protected String getSendTo(Auction auction) {
        throw new RuntimeException("getSendTo must be override.");
    }

    protected String getMessage(Auction auction) {
        throw new RuntimeException("getMessage must be override.");
    }

    protected boolean isTargetTransaction(Auction auction) {
        throw new RuntimeException("isNotificationTarget must be override.");
    }

}
