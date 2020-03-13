package application;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TestHelper {

    public static User getDefaultSeller() {
        String firstName = "Clark";
        String lastName = "Kent";
        String userEmail = "kent@example.com";
        String userName = "k-clark";
        String password = "PASSWORD";
        return new User(firstName, lastName, userEmail, userName, password);
    }

    public static User getDefaultBidder() {
        String firstName = "Eiji";
        String lastName = "Kinoshita";
        String userEmail = "kinoshita@example.com";
        String userName = "e-kinoshita";
        String password = "PASSWORD2";
        return new User(firstName, lastName, userEmail, userName, password);
    }

    public static Users setUpUsers() {
        Users users = new Users();
        User seller = getDefaultSeller();
        User preferredSeller = getPreferredSeller();
        User bidder = getDefaultBidder();
        users.register(seller);
        users.register(preferredSeller);
        users.register(bidder);
        users.promoteToSeller(seller);
        users.promoteToPreferredSeller(preferredSeller);
        return users;
    }

    public static Auction getDefaultAuction(User loggedinUser) {
        String itemName = "item-name";
        String itemDescription = "説明文";
        ItemCategory itemCategory = ItemCategory.OTHER;
        Integer startingPrice = new Integer(1000);
        LocalDateTime startTime = LocalDateTime.of(3020, 3, 10, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(3020, 3, 11, 9, 59, 59);
        return new Auction(loggedinUser, itemName, itemDescription, itemCategory, startingPrice, startTime, endTime);
    }

    public static Auction getCheapAuction(User loggedinUser) {
        String itemName = "item-name";
        String itemDescription = "説明文";
        ItemCategory itemCategory = ItemCategory.OTHER;
        Integer startingPrice = new Integer(10);
        LocalDateTime startTime = LocalDateTime.of(2020, 3, 10, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 3, 11, 9, 59, 59);
        return new Auction(loggedinUser, itemName, itemDescription, itemCategory, startingPrice, startTime, endTime);
    }

    public static Auction getDownloadSoftwareAuction(User loggedinUser) {
        String itemName = "item-name";
        String itemDescription = "説明文";
        ItemCategory itemCategory = ItemCategory.DOWNLOAD_SOFTWARE;
        Integer startingPrice = new Integer(1000);
        LocalDateTime startTime = LocalDateTime.of(2020, 3, 10, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 3, 11, 9, 59, 59);
        return new Auction(loggedinUser, itemName, itemDescription, itemCategory, startingPrice, startTime, endTime);
    }

    public static Auction getCarAuction(User loggedinUser) {
        String itemName = "item-name-car";
        String itemDescription = "説明文";
        ItemCategory itemCategory = ItemCategory.CAR;
        Integer startingPrice = new Integer(1000);
        LocalDateTime startTime = LocalDateTime.of(2020, 3, 10, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 3, 11, 9, 59, 59);
        return new Auction(loggedinUser, itemName, itemDescription, itemCategory, startingPrice, startTime, endTime);
    }

    public static User getPreferredSeller() {
        String firstName = "Devid";
        String lastName = "Bernstein";
        String userEmail = "devid@example.com";
        String userName = "d-bernstein";
        String password = "PASSWORD";
        return new User(firstName, lastName, userEmail, userName, password);
    }

    public static long ldtToEpochMilliSecond(LocalDateTime ldt) {
        return ldt.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
    }


}
