package application;

import com.tobeagile.training.ebaby.services.PostOffice;

public class WithoutBidAuctionCloseNotifier extends AuctionCloseNotifier {

    public WithoutBidAuctionCloseNotifier() {
        super(null);
    }

    @Override
    public void process(Auction auction) {
        String message = String.format(
                "Sorry, your auction for <%s> did not have any bidders",
                auction.itemName);
        PostOffice postOffice = PostOffice.getInstance();
        postOffice.sendEMail(auction.seller.userEmail, message);
    }
}
