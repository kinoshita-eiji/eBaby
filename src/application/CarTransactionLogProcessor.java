package application;

import com.tobeagile.training.ebaby.services.AuctionLogger;

//TODO Ask about Logging Format
public class CarTransactionLogProcessor extends OnCloseProcessor {

    public CarTransactionLogProcessor(OnCloseProcessor processor) {
        super(processor);
    }

    public String getFileName() {
        return "C:\\workspace\\eBaby\\log\\car-transaction.log";
    }

    public void process(Auction auction) {
        String message = String.format("itemName:%s seller:%s bidder:%s bidPrice:%s",
                auction.itemName,
                auction.seller.userName,
                auction.highestBidder != null ? auction.highestBidder.userName : "---",
                auction.highestPrice);

        if (auction.itemCategory == ItemCategory.CAR) {
            AuctionLogger.getInstance().log(getFileName(), message);
        }
        super.process(auction);
    }


}
