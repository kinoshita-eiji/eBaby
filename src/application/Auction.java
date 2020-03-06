package application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Auction {
    User seller;
    String itemName;
    String itemDescription;
    ItemCategory itemCategory;
    Integer startingPrice;
    LocalDateTime startTime;
    LocalDateTime endTime;
    AuctionStatus status;
    Integer highestPrice;
    User highestBidder;
    BigDecimal sellerAmount;
    BigDecimal bidderAmount;

    public Auction(User seller, String itemName, String itemDescription, ItemCategory itemCategory, Integer startingPrice, LocalDateTime startTime,
            LocalDateTime endTime) {
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

}
