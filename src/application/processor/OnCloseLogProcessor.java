package application.processor;

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
}
