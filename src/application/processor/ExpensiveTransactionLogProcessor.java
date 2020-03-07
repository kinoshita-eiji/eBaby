package application.processor;

import com.tobeagile.training.ebaby.services.AuctionLogger;

import application.Auction;

public class ExpensiveTransactionLogProcessor extends OnCloseLogProcessor {

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
        if (isLoggingTargetTransaction(auction)) {
            AuctionLogger.getInstance().log(getFileName(), getLogMessage(auction));
        }
        super.process(auction);
    }


}
