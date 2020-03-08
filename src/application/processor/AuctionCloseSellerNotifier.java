package application.processor;

import com.tobeagile.training.ebaby.services.PostOffice;

import application.Auction;
import application.User;

public class AuctionCloseSellerNotifier extends AuctionCloseNotifier {

    public AuctionCloseSellerNotifier(OnCloseProcessor processor) {
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
            String messageForSeller = String.format(
                    "Your <%s> auction sold to bidder <%s> for <%s>.",
                    itemName,
                    highestBidder.getUserEmail(),
                    highestPrice);
            postOffice.sendEMail(seller.getUserEmail(), messageForSeller);
        } else {
            String message = String.format(
                    "Sorry, your auction for <%s> did not have any bidders",
                    auction.getItemName());
            postOffice.sendEMail(auction.getSeller().getUserEmail(), message);
        }

        super.process(auction);
    }

}
