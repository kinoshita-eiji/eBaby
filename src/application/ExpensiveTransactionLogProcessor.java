package application;

import com.tobeagile.training.ebaby.services.AuctionLogger;

public class ExpensiveTransactionLogProcessor extends OnCloseProcessor {

    public ExpensiveTransactionLogProcessor(OnCloseProcessor processor) {
        super(processor);
    }

    public String getFileName() {
        return "C:\\workspace\\eBaby\\log\\expensive-transaction.log";
    }

    public void process(Auction auction) {

//        BetterAuctionLogger logger =  new BetterAuctionLogger(getFileName());
//        if (10000 <= auction.highestPrice) {
//            logger(auction).log();
//        }
//        super.process(auction);

        String message = String.format("itemName:%s seller:%s bidder:%s bidPrice:%s",
                auction.itemName,
                auction.seller.userName,
                auction.hasBid() ? auction.getHighestBidder().userName : "---",
                auction.getHighestPrice());

        if (10000 <= auction.getHighestPrice()) {
            AuctionLogger.getInstance().log(getFileName(), message);
        }
        super.process(auction);
    }


}
