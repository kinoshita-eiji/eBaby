package application.processor;

import com.tobeagile.training.ebaby.services.PostOffice;

import application.Auction;

public class WithoutBidAuctionCloseNotifier extends AuctionCloseNotifier {

    public WithoutBidAuctionCloseNotifier() {
        super(null);
    }

    @Override
    public void process(Auction auction) {
        String message = String.format(
                "Sorry, your auction for <%s> did not have any bidders",
                auction.getItemName());
        PostOffice postOffice = PostOffice.getInstance();
        postOffice.sendEMail(auction.getSeller().getUserEmail(), message);
    }
}
