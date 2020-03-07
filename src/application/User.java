package application;

import java.time.LocalDateTime;

import application.exception.AuctionIsNotStartedException;
import application.exception.InvalidAuctionTimeException;
import application.exception.InvalidBidException;
import application.exception.NotAuthenticatedException;
import application.exception.NotAuthorizedAsSellerException;

public class User {
    String firstName;
    String lastName;
    String userEmail;
    String userName;
    String password;
    boolean isLoggedIn;
    boolean isSeller;
    boolean isPreferredSeller;

    User(String firstName, String lastName, String userEmail, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userEmail = userEmail;
        this.userName = userName;
        this.password = password;
    }

    public Auction createAuction(String itemName, String itemDescription, ItemCategory itemCategory, Integer startingPrice, LocalDateTime startTime, LocalDateTime endTime) {
        if (!this.isLoggedIn) {
            throw new NotAuthenticatedException("You are not logged in");
        }
        if (!this.isSeller && !this.isPreferredSeller) {
            throw new NotAuthorizedAsSellerException("You are not a seller");
        }
        if (!startTime.isAfter(LocalDateTime.now())) {
            throw new InvalidAuctionTimeException("Start time must be in future");
        }
        if (!endTime.isAfter(startTime)) {
            throw new InvalidAuctionTimeException("end time must be greater than start time");
        }
        return new Auction(this, itemName, itemDescription, itemCategory, startingPrice, startTime, endTime);
    }

    public void bid(Auction auction, Integer price) {
        if (!this.isLoggedIn) {
            throw new NotAuthenticatedException("You are not logged in");
        }
        if (this.userName.equals(auction.seller.userName)) {
            throw new InvalidBidException("Bidder can't bid own auction");
        }
        if (auction.status != AuctionStatus.STARTED) {
            throw new AuctionIsNotStartedException("Auction is not started");
        }
        if (price <= auction.highestPrice) {
            throw new InvalidBidException("Bid price must be higher than current price");
        }

        auction.highestBidder = this;
        auction.highestPrice = price;
    }

}
