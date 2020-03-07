package application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import application.exception.AuctionIsNotStartedException;
import application.exception.InvalidAuctionTimeException;
import application.exception.InvalidBidException;

public class Auction {
    User seller;
    String itemName;
    String itemDescription;
    ItemCategory itemCategory;
    Integer startingPrice;
    LocalDateTime startTime;
    LocalDateTime endTime;
    AuctionStatus status;
    BigDecimal sellerAmount;
    BigDecimal bidderAmount;

    Integer highestPrice;
    User highestBidder;

    public Auction(User seller, String itemName, String itemDescription, ItemCategory itemCategory, Integer startingPrice, LocalDateTime startTime,
            LocalDateTime endTime) {

        if (!startTime.isAfter(LocalDateTime.now())) {
            throw new InvalidAuctionTimeException("Start time must be in future");
        }
        if (!endTime.isAfter(startTime)) {
            throw new InvalidAuctionTimeException("end time must be greater than start time");
        }

        this.seller = seller;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemCategory = itemCategory;
        this.startingPrice = startingPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AuctionStatus.UNSTARTED;
        this.highestPrice = startingPrice;
    }

    public void onStart() {
        this.status = AuctionStatus.STARTED;
    }

    public void onClose() {
        this.status = AuctionStatus.CLOSED;
        OnCloseProcessor processor = OnCloseProcessorFactory.getProcessor(this);
        processor.process(this);

    }

    public void acceptBid(Bid bid) {
        if (this.status != AuctionStatus.STARTED) {
            throw new AuctionIsNotStartedException("Auction is not started");
        }
        if (bid.price <= this.highestPrice) {
            throw new InvalidBidException("Bid price must be higher than current price");
        }
        this.highestBidder = bid.bidder;
        this.highestPrice = bid.price;

    }

}
