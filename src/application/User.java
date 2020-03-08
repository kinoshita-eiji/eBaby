package application;

import application.exception.NotAuthenticatedException;
import application.role.Bidder;
import application.role.Role;

public class User {
    private String firstName;
    private String lastName;
    private String userEmail;
    private String userName;
    private String password;
    private boolean isLoggedIn;
    private Role role;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    User(String firstName, String lastName, String userEmail, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userEmail = userEmail;
        this.userName = userName;
        this.password = password;
        this.role = new Bidder();
    }

    public Auction createAuction(Auction auction) {
        if (!this.isLoggedIn) {
            throw new NotAuthenticatedException("You are not logged in");
        }
        auction.setSeller(this);
        return getRole().createAuction(auction);
    }

    public void offerBid(Auction auction, Integer price) {
        if (!this.isLoggedIn) {
            throw new NotAuthenticatedException("You are not logged in");
        }

        Bid bid = new Bid(this, price);
        getRole().offerBid(auction, bid);

    }

    public boolean isSeller() {
        return getRole().isSeller();
    }

    public boolean isPreferredSeller() {
        return getRole().isPreferredSeller();
    }

}
