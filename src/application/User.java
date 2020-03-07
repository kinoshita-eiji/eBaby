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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        return role.createAuction(auction);
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
