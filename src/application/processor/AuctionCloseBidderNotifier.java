package application.processor;

import application.Auction;

public class AuctionCloseBidderNotifier extends AuctionCloseNotifier {

    public AuctionCloseBidderNotifier(OnCloseProcessor processor) {
        super(processor);
    }

    public String getSendTo(Auction auction) {
        return auction.getHighestBidder().getUserEmail();
    }

    public String getMessage(Auction auction) {
        return String.format(
                "Congratulations! You won an auction for a <%s> from <%s> for <%s>.",
                auction.getItemName(),
                auction.getSeller().getUserEmail(),
                auction.getHighestPrice());
    }

    public boolean isTargetTransaction(Auction auction) {
        return auction.hasBid();
    }

}
