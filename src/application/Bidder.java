package application;

public class Bidder extends Role {

    public Bidder() {
    }

    public boolean isSeller() {
        return false;
    }

    public boolean isPreferredSeller() {
        return false;
    }

    public void offerBid(Auction auction, Bid bid) {
        auction.acceptBid(bid);

    }
}
