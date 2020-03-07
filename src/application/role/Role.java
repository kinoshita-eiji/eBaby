package application.role;

import application.Auction;
import application.Bid;
import application.exception.NotAuthorizedAsSellerException;

public class Role {

    public Role() {
    }

    public Auction createAuction(Auction auction) {
        throw new NotAuthorizedAsSellerException("You are not seller.");
    }

    public void offerBid(Auction auction, Bid bid) {
        auction.acceptBid(bid);
    }

    public boolean isSeller() {
        throw new NotAuthorizedAsSellerException("You are not seller.");
    }

    public boolean isPreferredSeller() {
        throw new NotAuthorizedAsSellerException("You are not preferred seller.");
    }

}
