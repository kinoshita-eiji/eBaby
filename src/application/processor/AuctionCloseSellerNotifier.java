package application.processor;

import application.Auction;

public class AuctionCloseSellerNotifier extends AuctionCloseNotifier {

    public AuctionCloseSellerNotifier(OnCloseProcessor processor) {
        super(processor);
    }

    public String getSendTo(Auction auction) {
        return auction.getSeller().getUserEmail();
    }

    public String getMessage(Auction auction) {
        if (auction.hasBid()) {
            return String.format(
                    "Your <%s> auction sold to bidder <%s> for <%s>.",
                    auction.getItemName(),
                    auction.getHighestBidder().getUserEmail(),
                    auction.getHighestPrice());

        } else {
            return String.format(
                    "Sorry, your auction for <%s> did not have any bidders",
                    auction.getItemName());
        }
    }

    public boolean isTargetTransaction(Auction auction) {
        return true;
    }

}
