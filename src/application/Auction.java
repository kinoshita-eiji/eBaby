package application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import application.exception.AuctionIsNotStartedException;
import application.exception.InvalidBidException;
import application.processor.OnCloseProcessor;
import application.processor.OnCloseProcessorFactory;

public class Auction {
    private User seller;
    private String itemName;
    private String itemDescription;
    private ItemCategory itemCategory;
    private Integer startingPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private BigDecimal sellerAmount;
    private BigDecimal bidderAmount;
    private Bid highestBid;

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    public Integer getStartingPrice() {
        return startingPrice;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    public BigDecimal getSellerAmount() {
        return sellerAmount;
    }

    public void setSellerAmount(BigDecimal sellerAmount) {
        this.sellerAmount = sellerAmount;
    }

    public BigDecimal getBidderAmount() {
        return bidderAmount;
    }

    public void setBidderAmount(BigDecimal bidderAmount) {
        this.bidderAmount = bidderAmount;
    }

    public Bid getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(Bid highestBid) {
        this.highestBid = highestBid;
    }

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
        this.highestBid = new Bid(null, startingPrice);
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
        if (bid.bidder.getUserName().equals(this.seller.getUserName())) {
            throw new InvalidBidException("Bidder can't bid own auction");
        }
        if (this.status != AuctionStatus.STARTED) {
            throw new AuctionIsNotStartedException("Auction is not started");
        }
        if (bid.price <= getHighestPrice()) {
            throw new InvalidBidException("Bid price must be higher than current price");
        }
        this.setHighestBid(bid);
    }

    public User getHighestBidder() {
        return getHighestBid().bidder;
    }


    public Integer getHighestPrice() {
        return getHighestBid().price;
    }

    public boolean hasBid() {
        return getHighestBidder() != null;
    }

    public boolean isPreferred() {
        return this.seller.isPreferredSeller();
    }
}
