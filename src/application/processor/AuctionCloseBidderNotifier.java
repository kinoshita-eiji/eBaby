package application.processor;

import com.tobeagile.training.ebaby.services.PostOffice;

import application.Auction;
import application.User;

public class AuctionCloseBidderNotifier extends AuctionCloseNotifier {

    public AuctionCloseBidderNotifier(OnCloseProcessor processor) {
        super(processor);
    }

    @Override
    public void process(Auction auction) {
        String itemName = auction.getItemName();
        User highestBidder = auction.getHighestBidder();
        User seller = auction.getSeller();
        Integer highestPrice = auction.getHighestPrice();

        PostOffice postOffice = PostOffice.getInstance();

        if (auction.hasBid()) {
            String messageForHighestBidder = String.format(
                    "Congratulations! You won an auction for a <%s> from <%s> for <%s>.",
                    itemName,
                    seller.getUserEmail(),
                    highestPrice);
            postOffice.sendEMail(highestBidder.getUserEmail(), messageForHighestBidder);
        }
        super.process(auction);
    }
}
