package application;

import java.time.LocalDateTime;

import application.exception.NotAuthenticatedException;

public class User {
    String firstName;
    String lastName;
    String userEmail;
    String userName;
    String password;
    boolean isLoggedIn;
    Role role;

    User(String firstName, String lastName, String userEmail, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userEmail = userEmail;
        this.userName = userName;
        this.password = password;
        this.role = new Bidder();
    }

    public Auction createAuction(String itemName, String itemDescription, ItemCategory itemCategory, Integer startingPrice, LocalDateTime startTime, LocalDateTime endTime) {
        if (!this.isLoggedIn) {
            throw new NotAuthenticatedException("You are not logged in");
        }
        return role.createAuction(this, itemName, itemDescription, itemCategory, startingPrice, startTime, endTime);
    }

    public void offerBid(Auction auction, Integer price) {
        if (!this.isLoggedIn) {
            throw new NotAuthenticatedException("You are not logged in");
        }

        Bid bid = new Bid(this, price);
        role.offerBid(auction, bid);

    }

    public boolean isSeller() {
        return role.isSeller();
    }

    public boolean isPreferredSeller() {
        return role.isPreferredSeller();
    }

}
