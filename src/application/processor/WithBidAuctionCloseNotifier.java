package application.processor;

import com.tobeagile.training.ebaby.services.PostOffice;

import application.Auction;
import application.User;

public class WithBidAuctionCloseNotifier extends AuctionCloseNotifier {

    public WithBidAuctionCloseNotifier() {
        super(null);
    }

    @Override
    public void process(Auction auction) {

        String itemName = auction.getItemName();
        User highestBidder = auction.getHighestBidder();
        User seller = auction.getSeller();
        Integer highestPrice = auction.getHighestPrice();

        PostOffice postOffice = PostOffice.getInstance();

        String messageForSeller = String.format(
                "Your <%s> auction sold to bidder <%s> for <%s>.",
                itemName,
                highestBidder.getUserEmail(),
                highestPrice);
        postOffice.sendEMail(seller.getUserEmail(), messageForSeller);

        String messageForHighestBidder = String.format(
                "Congratulations! You won an auction for a <%s> from <%s> for <%s>.",
                itemName,
                seller.getUserEmail(),
                highestPrice);
        postOffice.sendEMail(highestBidder.getUserEmail(), messageForHighestBidder);


    }

}
