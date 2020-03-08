package application.processor;

import application.Auction;
import application.ItemCategory;

public class CarTransactionLogProcessor extends OnCloseLogProcessor {

    public CarTransactionLogProcessor(OnCloseProcessor processor) {
        super(processor);
    }

    public String getFileName() {
        return "C:\\workspace\\eBaby\\log\\car-transaction.log";
    }

    public boolean isLoggingTarget(Auction auction) {
        return auction.getItemCategory() == ItemCategory.CAR;
    }

}
