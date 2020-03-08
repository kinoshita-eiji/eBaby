package application.processor;

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

    public boolean isLoggingTarget(Auction auction) {
        return this.offHours.isOffHours();
    }

}
