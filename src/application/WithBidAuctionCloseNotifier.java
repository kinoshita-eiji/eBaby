package application;

import com.tobeagile.training.ebaby.services.PostOffice;

public class WithBidAuctionCloseNotifier extends AuctionCloseNotifier {

    public WithBidAuctionCloseNotifier() {
        super(null);
    }

    @Override
    public void process(Auction auction) {

        String itemName = auction.itemName;
        User highestBidder = auction.highestBidder;
        User seller = auction.seller;
        Integer highestPrice = auction.highestPrice;

        PostOffice postOffice = PostOffice.getInstance();

        String messageForSeller = String.format(
                "Your <%s> auction sold to bidder <%s> for <%s>.",
                itemName,
                highestBidder.userEmail,
                highestPrice);
        postOffice.sendEMail(seller.userEmail, messageForSeller);

        String messageForHighestBidder = String.format(
                "Congratulations! You won an auction for a <%s> from <%s> for <%s>.",
                itemName,
                seller.userEmail,
                highestPrice);
        postOffice.sendEMail(highestBidder.userEmail, messageForHighestBidder);


    }

}
