package application;

import com.tobeagile.training.ebaby.services.AuctionLogger;

public class ExpensiveTransactionLogProcessor extends OnCloseProcessor {

    public ExpensiveTransactionLogProcessor(OnCloseProcessor processor) {
        super(processor);
    }

    public boolean isLoggingTargetTransaction(Auction auction) {
        return 10000 <= auction.getHighestPrice();
    }

    public String getFileName() {
        return "C:\\workspace\\eBaby\\log\\expensive-transaction.log";
    }

    public void process(Auction auction) {

        String message = String.format("itemName:%s seller:%s bidder:%s bidPrice:%s",
                auction.itemName,
                auction.seller.userName,
                auction.hasBid() ? auction.getHighestBidder().userName : "---",
                auction.getHighestPrice());

        if (isLoggingTargetTransaction(auction)) {
            AuctionLogger.getInstance().log(getFileName(), message);
        }
        super.process(auction);
    }


}
