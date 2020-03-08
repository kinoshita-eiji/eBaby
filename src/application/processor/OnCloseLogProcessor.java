package application.processor;

import com.tobeagile.training.ebaby.services.AuctionLogger;

import application.Auction;

public class OnCloseLogProcessor extends OnCloseProcessor {

    public OnCloseLogProcessor(OnCloseProcessor processor) {
        super(processor);
    }

    protected String getLogMessage(Auction auction) {
        return String.format("itemName:%s seller:%s bidder:%s bidPrice:%s",
                auction.getItemName(),
                auction.getSeller().getUserName(),
                auction.hasBid() ? auction.getHighestBidder().getUserName() : "---",
                auction.getHighestPrice());
    }

    public void process(Auction auction) {
        if (isLoggingTarget(auction)) {
            AuctionLogger.getInstance().log(getFileName(), getLogMessage(auction));
        }
        super.process(auction);
    }

    protected String getFileName() {
        throw new RuntimeException("getFileName must be override.");
    }

    protected boolean isLoggingTarget(Auction auction) {
        return true;
    }

}
