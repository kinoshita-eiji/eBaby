package application.processor;

import application.Auction;

public class ExpensiveTransactionLogProcessor extends OnCloseLogProcessor {

    public ExpensiveTransactionLogProcessor(OnCloseProcessor processor) {
        super(processor);
    }

    public String getFileName() {
        return "C:\\workspace\\eBaby\\log\\expensive-transaction.log";
    }

    public boolean isLoggingTarget(Auction auction) {
        return 10000 <= auction.getHighestPrice();
    }

}
