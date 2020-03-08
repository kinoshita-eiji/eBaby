package application;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;

import com.tobeagile.training.ebaby.services.AuctionLogger;
import com.tobeagile.training.ebaby.services.Hours;
import com.tobeagile.training.ebaby.services.PostOffice;

import application.exception.AuctionIsNotStartedException;
import application.exception.BadCredentialException;
import application.exception.DuplicatedUserException;
import application.exception.InvalidAuctionTimeException;
import application.exception.InvalidBidException;
import application.exception.NotAuthenticatedException;
import application.exception.NotAuthorizedAsSellerException;
import application.processor.CarTransactionLogProcessor;
import application.processor.ExpensiveTransactionLogProcessor;
import application.processor.OffHourLogProcessor;
import application.processor.OnCloseProcessor;
import application.processor.OnCloseProcessorFactory;

public class AuctionTest {

    @Test
    public void UsersCanRegisterUser() {
        Users users = new Users();

        String firstName = "Clark";
        String lastName = "Kent";
        String userEmail = "kent@example.com";
        String userName = "k-clark";
        String password = "PASSWORD";
        User user = new User(firstName, lastName, userEmail, userName, password);
        User registeredUser = users.register(user);

        assertThat(registeredUser.getFirstName(), is(firstName));
        assertThat(registeredUser.getLastName(), is(lastName));
        assertThat(registeredUser.getUserEmail(), is(userEmail));
        assertThat(registeredUser.getUserName(), is(userName));
        assertThat(registeredUser.getPassword(), is(password));
    }

    @Test
    public void UsersCantRegisterSameNameUser() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        try {
            users.register(user);
            Assert.fail("Users can't register same userName");
        } catch (DuplicatedUserException e) {
        }
    }

    @Test
    public void UserCanLogin() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        User logginedUser = users.login(user.getUserName(), user.getPassword());
        assertThat(logginedUser.isLoggedIn(), is(true));

    }

    @Test
    public void UserCantLoginWithWrongPassword() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        try {
            users.login(user.getUserName(), "WRONG-PASSWORD");
            Assert.fail("Logged in with wrong password");
        } catch (BadCredentialException e) {
        }

    }

    @Test
    public void UserCantLoginWithoutRegistration() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        try {
            users.login("wrong-userName", user.getPassword());
            Assert.fail("Logged in with wrong userName");
        } catch (BadCredentialException e) {
        }

    }

    @Test
    public void UserCanLogoutWhenLoggedIn() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        users.login(user.getUserName(), user.getPassword());
        users.logout(user.getUserName());
        assertThat(user.isLoggedIn(), is(false));
    }

    @Test
    public void UserCantLogoutWhenNotLoggedIn() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        try {
            users.logout(user.getUserName());
            Assert.fail("You can't logout when not logged in");
        } catch (NotAuthenticatedException e) {
        }
    }

    @Test
    public void UserIsNotASeller() {
        User user = TestHelper.getDefaultSeller();
        assertThat(user.isSeller(), is(false));
    }

    @Test
    public void UserIsASeller() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        users.promoteToSeller(user);
        User loggedinUser = users.login(user.getUserName(), user.getPassword());
        assertThat(loggedinUser.isSeller(), is(true));
    }

    @Test
    public void SellerCanCreateAuction() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());

        String itemName = "item-name";
        String itemDescription = "item-description";
        ItemCategory itemCategory = ItemCategory.OTHER;
        Integer startingPrice = new Integer(1000);
        LocalDateTime startTime = LocalDateTime.of(2020, 3, 10, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 3, 11, 9, 59, 59);
        Auction auction = new Auction(seller, itemName, itemDescription, itemCategory, startingPrice, startTime, endTime);

        Auction createdAuction = seller.createAuction(auction);

        assertThat(createdAuction.getItemName(), is(itemName));
        assertThat(createdAuction.getSeller().getUserName(), is(seller.getUserName()));
        assertThat(createdAuction.getItemDescription(), is(itemDescription));
        assertThat(createdAuction.getStartingPrice(), is(startingPrice));
        assertThat(createdAuction.getStartTime(), is(startTime));
        assertThat(createdAuction.getEndTime(), is(endTime));
    }

    @Test
    public void NotSellerCantCreateAuction() {
        Users users = TestHelper.setUpUsers();
        User bidder = TestHelper.getDefaultBidder();
        User loggedinUser = users.login(bidder.getUserName(), bidder.getPassword());

        try {
            loggedinUser.createAuction(TestHelper.getDefaultAuction(loggedinUser));
            Assert.fail("Not seller can't create auction");
        } catch (NotAuthorizedAsSellerException e) {
        }
    }

    @Test
    public void SellerCantCreateAuctionWhenNotLoggedIn() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        users.promoteToSeller(user);

        try {
            user.createAuction(TestHelper.getDefaultAuction(user));
            Assert.fail("Seller can't create auction when not logged in");
        } catch (NotAuthenticatedException e) {
        }
    }

    @Test
    public void ActionStartTimeMustBeInFuture() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        users.promoteToSeller(user);
        User loggedinUser = users.login(user.getUserName(), user.getPassword());

        String itemName = "item-name";
        String itemDescription = "item-description";
        ItemCategory itemCategory = ItemCategory.OTHER;
        Integer startingPrice = new Integer(1000);
        LocalDateTime startTime = LocalDateTime.of(2020, 3, 9, 9, 59, 59);
        LocalDateTime endTime = LocalDateTime.of(2020, 3, 10, 10, 0, 0);
        Auction auction =  new Auction(loggedinUser, itemName, itemDescription, itemCategory, startingPrice, startTime, endTime);

        try {
            Auctions auctions = new MockedAuctions();
            auctions.setNow(LocalDateTime.of(2020, 3, 10, 9, 59, 59));
            auctions.create(auction);
            Assert.fail("Action start time must be in future");
        } catch (InvalidAuctionTimeException e) {
        }

    }

    @Test
    public void ActionEndTimeMustBeGreaterThanStartTime() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        users.promoteToSeller(user);
        User loggedinUser = users.login(user.getUserName(), user.getPassword());

        String itemName = "item-name";
        String itemDescription = "item-description";
        ItemCategory itemCategory = ItemCategory.OTHER;
        Integer startingPrice = new Integer(1000);
        LocalDateTime startTime = LocalDateTime.of(2020, 3, 11, 9, 59, 59);
        LocalDateTime endTime = LocalDateTime.of(2020, 3, 10, 10, 0, 0);
        Auction auction =  new Auction(loggedinUser, itemName, itemDescription, itemCategory, startingPrice, startTime, endTime);

        try {
            Auctions auctions = new Auctions();
            auctions.create(auction);
            Assert.fail("Action end time must be greater than start time");
        } catch (InvalidAuctionTimeException e) {
        }

    }

    @Test
    public void ActionEndTimeMustNotBeSameAsStartTime() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        users.promoteToSeller(user);
        User loggedinUser = users.login(user.getUserName(), user.getPassword());

        String itemName = "item-name";
        String itemDescription = "item-description";
        ItemCategory itemCategory = ItemCategory.OTHER;
        Integer startingPrice = new Integer(1000);
        LocalDateTime sameTime = LocalDateTime.of(2020, 3, 10, 10, 0, 0);
        Auction auction = new Auction(loggedinUser, itemName, itemDescription, itemCategory, startingPrice, sameTime, sameTime);

        try {
            Auctions auctions = new Auctions();
            auctions.create(auction);
            Assert.fail("Action end time must not be same as start time");
        } catch (InvalidAuctionTimeException e) {
        }
    }

    @Test
    public void AuctionCanStart() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());

        Auction createdAuction = TestHelper.getDefaultAuction(seller);

        assertThat(createdAuction.getStatus(), is(AuctionStatus.UNSTARTED));

        createdAuction.onStart();

        assertThat(createdAuction.getStatus(), is(AuctionStatus.STARTED));

    }

    @Test
    public void AuctionCanClose() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());

        Auction createdAuction = TestHelper.getDefaultAuction(seller);

        assertThat(createdAuction.getStatus(), is(AuctionStatus.UNSTARTED));

        createdAuction.onStart();
        createdAuction.onClose();

        assertThat(createdAuction.getStatus(), is(AuctionStatus.CLOSED));
    }

    @Test
    public void AuthenticatedBidderCanBidStartedAuction() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());

        User bidder = TestHelper.getDefaultBidder();
        bidder = users.login(bidder.getUserName(), bidder.getPassword());

        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        Integer bidPrice = new Integer(2000);
        bidder.offerBid(auction, bidPrice);
        assertThat(auction.getHighestBidder(), is(bidder));
        assertThat(auction.getHighestPrice(), is(bidPrice));
    }

    @Test
    public void UnauthenticatedBidderCantBidStartedAuction() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());

        User bidder = TestHelper.getDefaultBidder();

        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        Integer bidPrice = new Integer(2000);
        try {
            bidder.offerBid(auction, bidPrice);
            Assert.fail("Unauthenticated bidder can't bid started auction");
        } catch (NotAuthenticatedException e) {
        }
    }

    @Test
    public void AuthenticatedBidderCantBidUnstartedAuction() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());

        User bidder = TestHelper.getDefaultBidder();
        bidder = users.login(bidder.getUserName(), bidder.getPassword());

        Auction auction = TestHelper.getDefaultAuction(seller);

        Integer bidPrice = new Integer(2000);
        try {
            bidder.offerBid(auction, bidPrice);
            Assert.fail("Unstarted auction can't be bid");
        } catch (AuctionIsNotStartedException e) {
        }
    }

    @Test
    public void AuthenticatedBidderCantBidLowerThanStartingPrice() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());

        User bidder = TestHelper.getDefaultBidder();
        bidder = users.login(bidder.getUserName(), bidder.getPassword());

        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        try {
            Integer lowerPrice = new Integer(999);
            bidder.offerBid(auction, lowerPrice);
            Assert.fail("Bidder can't bid lower price than starting price.");
        } catch (InvalidBidException e) {
        }
    }

    @Test
    public void AuthenticatedBidderCantBidLowerPrice() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());

        User bidder = TestHelper.getDefaultBidder();
        bidder = users.login(bidder.getUserName(), bidder.getPassword());

        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        Integer bidPrice = new Integer(2000);
        bidder.offerBid(auction, bidPrice);

        try {
            Integer lowerPrice = new Integer(1999);
            bidder.offerBid(auction, lowerPrice);
            Assert.fail("Bidder can't bid lower price.");
        } catch (InvalidBidException e) {
        }
    }

    @Test
    public void AuthenticatedBidderCantBidOwnAuction() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());

        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        try {
            Integer lowerPrice = new Integer(2000);
            seller.offerBid(auction, lowerPrice);
            Assert.fail("Bidder can't bid own auction.");
        } catch (InvalidBidException e) {
        }
    }

    @Test
    public void AuctionClosedWithoutBidNotifyToSeller() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();
        PostOffice postOffice = PostOffice.getInstance();
        postOffice.clear();
        auction.onClose();
        assertThat(
                postOffice.findEmail(seller.getUserEmail(), "Sorry, your auction for <item-name> did not have any bidders"),
                is("<sendEMail address=\"kent@example.com\" >Sorry, your auction for <item-name> did not have any bidders</sendEmail>\n"));
    }

    @Test
    public void AuctionClosedWithBidNotifyToSeller() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        PostOffice postOffice = PostOffice.getInstance();
        postOffice.clear();
        auction.onClose();
        assertThat(
                postOffice.findEmail(seller.getUserEmail(),
                        "Your <item-name> auction sold to bidder <kinoshita@example.com> for <2000>."),
                is("<sendEMail address=\"kent@example.com\" >Your <item-name> auction sold to bidder <kinoshita@example.com> for <2000>.</sendEmail>\n"));
    }

    @Test
    public void AuctionClosedWithBidNotifyToHighestBidder() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        PostOffice postOffice = PostOffice.getInstance();
        postOffice.clear();
        auction.onClose();
        assertThat(
                postOffice.findEmail(bidder.getUserEmail(),
                        "Congratulations! You won an auction for a <item-name> from <kent@example.com> for <2000>."),
                is("<sendEMail address=\"kinoshita@example.com\" >Congratulations! You won an auction for a <item-name> from <kent@example.com> for <2000>.</sendEmail>\n"));
    }

    @Test
    public void CalculateSallerAmount() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(1999);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        auction.onClose();
        assertThat(auction.getSellerAmount(), is(new BigDecimal(1960)));
    }

    @Test
    public void CalculateBidderAmountForDownloadSoftware() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getDownloadSoftwareAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        auction.onClose();
        assertThat(auction.getBidderAmount(), is(new BigDecimal(bidPrice)));
    }

    @Test
    public void CalculateBidderAmountForOther() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        auction.onClose();
        assertThat(auction.getBidderAmount(), is(new BigDecimal(bidPrice + 10)));
    }

    @Test
    public void CalculateBidderAmountForCheapCar() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getCarAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(49999);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        auction.onClose();
        assertThat(auction.getBidderAmount(), is(new BigDecimal(bidPrice + 1000)));
    }

    @Test
    public void CalculateBidderAmountForExpensiveCar() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getCarAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(50001);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        auction.onClose();
        assertThat(auction.getBidderAmount(), is(new BigDecimal(bidPrice + 1000 + 2000)));
    }

    @Test
    public void CarTransactionIsLoggedToCarLog() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getCarAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(50001);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        String fileName = "C:\\workspace\\eBaby\\log\\car-transaction.log";
        cleanUpLogfile(fileName);

        auction.onClose();

        String message = String.format("itemName:%s seller:%s bidder:%s bidPrice:%s",
                "item-name-car",
                "k-clark",
                "e-kinoshita",
                bidPrice);

        AuctionLogger logger = AuctionLogger.getInstance();
        assertThat(logger.findMessage(fileName, message), is(true));
    }

    @Test
    public void OtherTransactionIsNotLoggedToCarLog() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(50001);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        CarTransactionLogProcessor processor = new CarTransactionLogProcessor(null);
        String fileName = processor.getFileName();
        cleanUpLogfile(fileName);

        auction.onClose();

        assertThat(new File(fileName).exists(), is(false));
    }

    @Test
    public void ExpensiveTransactionIsLogged() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getCarAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(10000);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        AuctionLogger logger = AuctionLogger.getInstance();
        String fileName = "C:\\workspace\\eBaby\\log\\expensive-transaction.log";
        cleanUpLogfile(fileName);

        auction.onClose();

        String message = String.format("itemName:%s seller:%s bidder:%s bidPrice:%s",
                "item-name-car",
                "k-clark",
                "e-kinoshita",
                bidPrice);

        assertThat(logger.findMessage(fileName, message), is(true));
    }

    @Test
    public void CheapTransactionIsNotLogged() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getCarAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(9999);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        ExpensiveTransactionLogProcessor processor = new ExpensiveTransactionLogProcessor(null);
        String fileName = processor.getFileName();
        cleanUpLogfile(fileName);

        auction.onClose();

        assertThat(new File(fileName).exists(), is(false));
    }

    @Test
    public void ShippingFeeOfPreferredSellerIsFreeWhenPriceIsOverThan50() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getPreferredSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getCheapAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(50);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        auction.onClose();

        assertThat(auction.getBidderAmount(), is(new BigDecimal(bidPrice)));
    }

    @Test
    public void ShippingFeeOfPreferredSellerIsNotFreeWhenPriceIsLessThan50() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getPreferredSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getCheapAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(49);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        auction.onClose();

        assertThat(auction.getBidderAmount(), is(new BigDecimal(bidPrice + 10)));
    }

    @Test
    public void ShippingFeeOfPreferredSellerIsHalfForCar() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getPreferredSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getCarAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        auction.onClose();

        assertThat(auction.getBidderAmount(), is(new BigDecimal(bidPrice + 1000 / 2)));
    }

    @Test
    public void TransactionFeeOfPreferredSellerIs1Percent() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getPreferredSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        auction.onClose();

        assertThat(auction.getSellerAmount(), is(new BigDecimal(bidPrice - 20)));
    }

    @Test
    public void LoggingWhenOffHours() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        AuctionLogger logger = AuctionLogger.getInstance();
        String fileName = "C:\\workspace\\eBaby\\log\\offhour-transaction.log";
        logger.clearLog(fileName);

        OnCloseProcessor processor = OnCloseProcessorFactory.getProcessor(auction, new MockOffHours(true));
        processor.process(auction);

        String message = String.format("itemName:%s seller:%s bidder:%s bidPrice:%s",
                "item-name",
                "k-clark",
                "e-kinoshita",
                bidPrice);

        assertThat(logger.findMessage(fileName, message), is(true));

    }

    @Test
    public void NoLoggingWhenOnHours() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.getUserName(), bidder.getPassword());
        bidder.offerBid(auction, bidPrice);

        String fileName = new OffHourLogProcessor(null, null).getFileName();
        cleanUpLogfile(fileName);

        OnCloseProcessor processor = OnCloseProcessorFactory.getProcessor(auction, new MockOffHours(false));
        processor.process(auction);

        assertThat(new File(fileName).exists(), is(false));

    }

    @Test
    public void AuctionIsNotStartedBeforeStartTime() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getDefaultAuction(seller);

        Auctions auctions = new Auctions();
        auctions.create(auction);

        AuctionTimer timer = new AuctionTimer();
        timer.checkAuction(auctions);

        long now = TestHelper.ldtToEpochMilliSecond(auction.getStartTime()) - 1000;
        timer.timerTick(now);

        Auction handledAuction = auctions.getList().get(0);
        assertThat(handledAuction.getStatus(), is(AuctionStatus.UNSTARTED));
    }

    @Test
    public void AuctionIsStartedBetweenWindowTime() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getDefaultAuction(seller);

        Auctions auctions = new Auctions();
        auctions.create(auction);

        AuctionTimer timer = new AuctionTimer();
        timer.checkAuction(auctions);

        long now = TestHelper.ldtToEpochMilliSecond(auction.getStartTime()) + 1000;
        timer.timerTick(now);

        Auction handledAuction = auctions.getList().get(0);
        assertThat(handledAuction.getStatus(), is(AuctionStatus.STARTED));
    }

    @Test
    public void AuctionIsClosedAfterWindowTime() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.getUserName(), seller.getPassword());
        Auction auction = TestHelper.getDefaultAuction(seller);

        Auctions auctions = new Auctions();
        auctions.create(auction);

        AuctionTimer timer = new AuctionTimer();
        timer.checkAuction(auctions);

        long now = TestHelper.ldtToEpochMilliSecond(auction.getEndTime()) + 1000;
        timer.timerTick(now);

        Auction handledAuction = auctions.getList().get(0);
        assertThat(handledAuction.getStatus(), is(AuctionStatus.CLOSED));
    }

    private void cleanUpLogfile(String fileName) {
        if (new File(fileName).exists()) {
            AuctionLogger logger = AuctionLogger.getInstance();
            logger.clearLog(fileName);
        }
    }

    class MockedAuctions extends Auctions {
        private LocalDateTime now;

        public LocalDateTime now() {
            return this.now;
        }

        public void setNow(LocalDateTime ldt) {
            this.now = ldt;
        }
    }

    class MockOffHours implements Hours {
        boolean returnValue;

        public MockOffHours(boolean returnValue) {
            this.returnValue = returnValue;
        }

        public boolean isOffHours() {
            return returnValue;
        }
    }
}
