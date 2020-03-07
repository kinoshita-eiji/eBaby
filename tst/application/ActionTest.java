package application;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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

public class ActionTest {

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

        assertThat(registeredUser.firstName, is(firstName));
        assertThat(registeredUser.lastName, is(lastName));
        assertThat(registeredUser.userEmail, is(userEmail));
        assertThat(registeredUser.userName, is(userName));
        assertThat(registeredUser.password, is(password));
    }

    @Test
    public void UsersCantRegisterSameNameUser() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        try {
            users.register(user);
            Assert.fail("同じユーザ名で登録できてはいけない");
        } catch (DuplicatedUserException e) {
        }
    }

    @Test
    public void UserCanLogin() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        User logginedUser = users.login(user.userName, user.password);
        assertThat(logginedUser.isLoggedIn, is(true));

    }

    @Test
    public void UserCantLoginWithWrongPassword() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        try {
            users.login(user.userName, "WRONG-PASSWORD");
            Assert.fail("Logged in with wrong password");
        } catch (BadCredentialException e) {
        }

    }

    @Test
    public void UserCantLoginWithoutRegistration() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        try {
            users.login("wrong-userName", user.password);
            Assert.fail("Logged in with wrong userName");
        } catch (BadCredentialException e) {
        }

    }

    @Test
    public void UserCanLogoutWhenLoggedIn() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        users.login(user.userName, user.password);
        users.logout(user.userName);
        assertThat(user.isLoggedIn, is(false));
    }

    @Test
    public void UserCantLogoutWhenNotLoggedIn() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        try {
            users.logout(user.userName);
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
        User loggedinUser = users.login(user.userName, user.password);
        assertThat(loggedinUser.isSeller(), is(true));
    }

    @Test
    public void SellerCanCreateAuction() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);

        String itemName = "item-name";
        String itemDescription = "説明文";
        ItemCategory itemCategory = ItemCategory.OTHER;
        Integer startingPrice = new Integer(1000);
        LocalDateTime startTime = LocalDateTime.of(2020, 3, 10, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 3, 11, 9, 59, 59);

        Auction createdAuction = seller.createAuction(
                itemName, itemDescription, itemCategory,startingPrice, startTime, endTime);

        assertThat(createdAuction.itemName, is(itemName));
        assertThat(createdAuction.seller.userName, is(seller.userName));
        assertThat(createdAuction.itemDescription, is(itemDescription));
        assertThat(createdAuction.startingPrice, is(startingPrice));
        assertThat(createdAuction.startTime, is(startTime));
        assertThat(createdAuction.endTime, is(endTime));
    }

    @Test
    public void NotSellerCantCreateAuction() {
        Users users = TestHelper.setUpUsers();
        User bidder = TestHelper.getDefaultBidder();
        User loggedinUser = users.login(bidder.userName, bidder.password);

        try {
            loggedinUser.createAuction("item-name", "item-description", ItemCategory.OTHER, 1000, LocalDateTime.of(2020, 3, 6, 10, 0, 0),
                    LocalDateTime.of(2020, 3, 8, 9, 59, 59));
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
            user.createAuction("item-name", "item-description", ItemCategory.OTHER, 1000, LocalDateTime.of(2020, 3, 4, 10, 0, 0),
                    LocalDateTime.of(2020, 3, 5, 9, 59, 59));
            Assert.fail("Seller can't create auction when not logged in");
        } catch (NotAuthenticatedException e) {
        }
    }

    @Test
    public void ActionStartTimeMustBeInFuture() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        users.promoteToSeller(user);
        User loggedinUser = users.login(user.userName, user.password);

        try {
            loggedinUser.createAuction("item-name", "item-description", ItemCategory.OTHER, 1000, LocalDateTime.of(2020, 2, 4, 10, 0, 0),
                    LocalDateTime.of(2020, 3, 5, 9, 59, 59));
            Assert.fail("Action start time must be in future");
        } catch (InvalidAuctionTimeException e) {
        }

    }

    @Test
    public void ActionEndTimeMustBeGreaterThanStartTime() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        users.promoteToSeller(user);
        User loggedinUser = users.login(user.userName, user.password);

        try {
            loggedinUser.createAuction("item-name", "item-description", ItemCategory.OTHER, 1000, LocalDateTime.of(2020, 3, 10, 10, 0, 0),
                    LocalDateTime.of(2020, 3, 9, 9, 59, 59));
            Assert.fail("Action end time must be greater than start time");
        } catch (InvalidAuctionTimeException e) {
        }

    }

    @Test
    public void ActionEndTimeMustNotBeSameAsStartTime() {
        Users users = TestHelper.setUpUsers();
        User user = TestHelper.getDefaultSeller();
        users.promoteToSeller(user);
        User loggedinUser = users.login(user.userName, user.password);

        LocalDateTime sameTime = LocalDateTime.of(2020, 3, 10, 10, 0, 0);

        try {
            loggedinUser.createAuction("item-name", "item-description", ItemCategory.OTHER, 1000, sameTime, sameTime);
            Assert.fail("Action end time must not be same as start time");
        } catch (InvalidAuctionTimeException e) {
        }
    }

    @Test
    public void AuctionCanStart() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);

        Auction createdAuction = TestHelper.getDefaultAuction(seller);

        assertThat(createdAuction.status, is(AuctionStatus.UNSTARTED));

        createdAuction.onStart();

        assertThat(createdAuction.status, is(AuctionStatus.STARTED));

    }

    @Test
    public void AuctionCanClose() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);

        Auction createdAuction = TestHelper.getDefaultAuction(seller);

        assertThat(createdAuction.status, is(AuctionStatus.UNSTARTED));

        createdAuction.onStart();
        createdAuction.onClose();

        assertThat(createdAuction.status, is(AuctionStatus.CLOSED));
    }

    @Test
    public void AuthenticatedBidderCanBidStartedAuction() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);

        User bidder = TestHelper.getDefaultBidder();
        bidder = users.login(bidder.userName, bidder.password);

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
        seller = users.login(seller.userName, seller.password);

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
        seller = users.login(seller.userName, seller.password);

        User bidder = TestHelper.getDefaultBidder();
        bidder = users.login(bidder.userName, bidder.password);

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
        seller = users.login(seller.userName, seller.password);

        User bidder = TestHelper.getDefaultBidder();
        bidder = users.login(bidder.userName, bidder.password);

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
        seller = users.login(seller.userName, seller.password);

        User bidder = TestHelper.getDefaultBidder();
        bidder = users.login(bidder.userName, bidder.password);

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
        seller = users.login(seller.userName, seller.password);

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
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();
        PostOffice postOffice = PostOffice.getInstance();
        postOffice.clear();
        auction.onClose();
        assertThat(
                postOffice.findEmail(seller.userEmail, "Sorry, your auction for <item-name> did not have any bidders"),
                is("<sendEMail address=\"kent@example.com\" >Sorry, your auction for <item-name> did not have any bidders</sendEmail>\n"));
    }

    @Test
    public void AuctionClosedWithBidNotifyToSeller() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        PostOffice postOffice = PostOffice.getInstance();
        postOffice.clear();
        auction.onClose();
        assertThat(
                postOffice.findEmail(seller.userEmail,
                        "Your <item-name> auction sold to bidder <kinoshita@example.com> for <2000>."),
                is("<sendEMail address=\"kent@example.com\" >Your <item-name> auction sold to bidder <kinoshita@example.com> for <2000>.</sendEmail>\n"));
    }

    @Test
    public void AuctionClosedWithBidNotifyToHighestBidder() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        PostOffice postOffice = PostOffice.getInstance();
        postOffice.clear();
        auction.onClose();
        assertThat(
                postOffice.findEmail(bidder.userEmail,
                        "Congratulations! You won an auction for a <item-name> from <kent@example.com> for <2000>."),
                is("<sendEMail address=\"kinoshita@example.com\" >Congratulations! You won an auction for a <item-name> from <kent@example.com> for <2000>.</sendEmail>\n"));
    }

    @Test
    public void calculateSallerAmount() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(1999);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        auction.onClose();
        assertThat(auction.sellerAmount, is(new BigDecimal(1960)));
    }

    @Test
    public void CalculateBidderAmountForDownloadSoftware() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getDownloadSoftwareAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        auction.onClose();
        assertThat(auction.bidderAmount, is(new BigDecimal(bidPrice)));
    }

    @Test
    public void CalculateBidderAmountForOther() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        auction.onClose();
        assertThat(auction.bidderAmount, is(new BigDecimal(bidPrice + 10)));
    }

    @Test
    public void CalculateBidderAmountForCheapCar() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getCarAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(49999);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        auction.onClose();
        assertThat(auction.bidderAmount, is(new BigDecimal(bidPrice + 1000)));
    }

    @Test
    public void CalculateBidderAmountForExpensiveCar() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getCarAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(50001);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        auction.onClose();
        assertThat(auction.bidderAmount, is(new BigDecimal(bidPrice + 1000 + 2000)));
    }

    @Test
    public void CarTransactionIsLoggedToCarLog() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getCarAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(50001);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        AuctionLogger logger = AuctionLogger.getInstance();
        String fileName = "C:\\workspace\\eBaby\\log\\car-transaction.log";
        logger.clearLog(fileName);

        auction.onClose();

        String message = String.format("itemName:%s seller:%s bidder:%s bidPrice:%s",
                "item-name-car",
                "k-clark",
                "e-kinoshita",
                bidPrice);

        assertThat(logger.findMessage(fileName, message), is(true));
    }

    @Test
    public void OtherTransactionIsNotLoggedToCarLog() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(50001);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        AuctionLogger logger = AuctionLogger.getInstance();
        String fileName = "C:\\workspace\\eBaby\\log\\car-transaction.log";
        logger.clearLog(fileName);

        auction.onClose();

        String message = String.format("itemName:%s seller:%s bidder:%s bidPrice:%s",
                "item-name",
                "k-clark",
                "e-kinoshita",
                bidPrice);

        assertThat(logger.findMessage(fileName, message), is(false));
    }

    @Test
    public void ExpensiveTransactionIsLogged() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getCarAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(10000);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        AuctionLogger logger = AuctionLogger.getInstance();
        String fileName = "C:\\workspace\\eBaby\\log\\expensive-transaction.log";
        logger.clearLog(fileName);

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
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getCarAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(9999);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        AuctionLogger logger = AuctionLogger.getInstance();
        String fileName = "C:\\workspace\\eBaby\\log\\expensive-transaction.log";
        logger.clearLog(fileName);

        auction.onClose();

        String message = String.format("itemName:%s seller:%s bidder:%s bidPrice:%s",
                "item-name-car",
                "k-clark",
                "e-kinoshita",
                bidPrice);

        assertThat(logger.findMessage(fileName, message), is(false));
    }

    @Test
    public void ShippingFeeOfPreferredSellerIsFreeWhenPriceIsOverThan50() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getPreferredSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getCheapAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(50);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        auction.onClose();

        assertThat(auction.bidderAmount, is(new BigDecimal(bidPrice)));
    }

    @Test
    public void ShippingFeeOfPreferredSellerIsNotFreeWhenPriceIsLessThan50() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getPreferredSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getCheapAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(49);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        auction.onClose();

        assertThat(auction.bidderAmount, is(new BigDecimal(bidPrice + 10)));
    }

    @Test
    public void ShippingFeeOfPreferredSellerIsHalfForCar() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getPreferredSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getCarAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        auction.onClose();

        assertThat(auction.bidderAmount, is(new BigDecimal(bidPrice + 1000 / 2)));
    }

    @Test
    public void TransactionFeeOfPreferredSellerIs1Percent() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getPreferredSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        auction.onClose();

        assertThat(auction.sellerAmount, is(new BigDecimal(bidPrice - 20)));
    }

    @Test
    public void LoggingWhenOffHours() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.userName, bidder.password);
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
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getDefaultAuction(seller);
        auction.onStart();

        User bidder = TestHelper.getDefaultBidder();
        Integer bidPrice = new Integer(2000);
        bidder = users.login(bidder.userName, bidder.password);
        bidder.offerBid(auction, bidPrice);

        AuctionLogger logger = AuctionLogger.getInstance();
        String fileName = "C:\\workspace\\eBaby\\log\\offhour-transaction.log";
        logger.clearLog(fileName);

        OnCloseProcessor processor = OnCloseProcessorFactory.getProcessor(auction, new MockOffHours(false));
        processor.process(auction);

        String message = String.format("itemName:%s seller:%s bidder:%s bidPrice:%s",
                "item-name",
                "k-clark",
                "e-kinoshita",
                bidPrice);

        assertThat(logger.findMessage(fileName, message), is(false));

    }

    @Test
    public void AuctionIsNotStartedBeforeStartTime() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getDefaultAuction(seller);

        Auctions auctions = new Auctions();
        auctions.create(auction);

        BetterAuctionTimer timer = new BetterAuctionTimer();
        timer.checkAuction(auctions);

        long now = TestHelper.ldtToEpochMilliSecond(auction.startTime) - 1000;
        timer.timerTick(now);

        Auction handledAuction = auctions.getList().get(0);
        assertThat(handledAuction.status, is(AuctionStatus.UNSTARTED));
    }

    @Test
    public void AuctionIsStartedBetweenWindowTime() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getDefaultAuction(seller);

        Auctions auctions = new Auctions();
        auctions.create(auction);

        BetterAuctionTimer timer = new BetterAuctionTimer();
        timer.checkAuction(auctions);

        long now = TestHelper.ldtToEpochMilliSecond(auction.startTime) + 1000;
        timer.timerTick(now);

        Auction handledAuction = auctions.getList().get(0);
        assertThat(handledAuction.status, is(AuctionStatus.STARTED));
    }

    @Test
    public void AuctionIsClosedAfterWindowTime() {
        Users users = TestHelper.setUpUsers();
        User seller = TestHelper.getDefaultSeller();
        seller = users.login(seller.userName, seller.password);
        Auction auction = TestHelper.getDefaultAuction(seller);

        Auctions auctions = new Auctions();
        auctions.create(auction);

        BetterAuctionTimer timer = new BetterAuctionTimer();
        timer.checkAuction(auctions);

        long now = TestHelper.ldtToEpochMilliSecond(auction.endTime) + 1000;
        timer.timerTick(now);

        Auction handledAuction = auctions.getList().get(0);
        assertThat(handledAuction.status, is(AuctionStatus.CLOSED));
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
