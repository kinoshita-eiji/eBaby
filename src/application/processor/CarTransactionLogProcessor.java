package application.processor;

import com.tobeagile.training.ebaby.services.AuctionLogger;

import application.Auction;
import application.ItemCategory;

//TODO Ask about Logging Format
public class CarTransactionLogProcessor extends OnCloseLogProcessor {

    public CarTransactionLogProcessor(OnCloseProcessor processor) {
        super(processor);
    }

    public String getFileName() {
        return "C:\\workspace\\eBaby\\log\\car-transaction.log";
    }

    public void process(Auction auction) {
        if (auction.getItemCategory() == ItemCategory.CAR) {
            AuctionLogger.getInstance().log(getFileName(), getLogMessage(auction));
        }
        super.process(auction);
    }

}
