package application.role;

import application.Auction;

public class Seller extends Role {

    public Seller() {
    }

    public Auction createAuction(Auction auction) {
        return auction;
    }

    public boolean isSeller() {
        return true;
    }

}
