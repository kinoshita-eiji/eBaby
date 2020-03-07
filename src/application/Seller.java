package application;

import java.time.LocalDateTime;

public class Seller extends Role {

    public Seller() {
    }

    public Auction createAuction(User seller, String itemName, String itemDescription, ItemCategory itemCategory, Integer startingPrice, LocalDateTime startTime, LocalDateTime endTime) {
        return new Auction(seller, itemName, itemDescription, itemCategory, startingPrice, startTime, endTime);
    }

    public boolean isSeller() {
        return true;
    }

    public boolean isPreferredSeller() {
        return false;
    }
}
