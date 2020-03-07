package application.processor;

import com.tobeagile.training.ebaby.services.AuctionLogger;
import com.tobeagile.training.ebaby.services.Hours;

import application.Auction;

public class OffHourLogProcessor extends OnCloseLogProcessor {

    Hours offHours;

    public OffHourLogProcessor(OnCloseProcessor processor, Hours offhours) {
        super(processor);
        this.offHours = offhours;
    }

    public String getFileName() {
        return "C:\\workspace\\eBaby\\log\\offhour-transaction.log";
    }

    public void process(Auction auction) {
        if (this.offHours.isOffHours()) {
            AuctionLogger.getInstance().log(getFileName(), getLogMessage(auction));
        }
        super.process(auction);
    }


}
