package application;

import java.time.LocalDateTime;

import application.exception.NotAuthorizedAsSellerException;

public class Role {

    public Role() {
    }

    public Auction createAuction(User seller, String itemName, String itemDescription, ItemCategory itemCategory, Integer startingPrice, LocalDateTime startTime, LocalDateTime endTime) {
        throw new NotAuthorizedAsSellerException("You are not seller.");
    }

    public void offerBid(Auction auction, Bid bid) {
        throw new RuntimeException("You cant' bid");
    }

    public boolean isSeller() {
        throw new NotAuthorizedAsSellerException("You are not seller.");
    }

    public boolean isPreferredSeller() {
        throw new NotAuthorizedAsSellerException("You are not preferred seller.");
    }

}
