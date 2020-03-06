package application;

import com.tobeagile.training.ebaby.services.AuctionLogger;
import com.tobeagile.training.ebaby.services.Hours;

public class OffHourLogProcessor extends OnCloseProcessor {

    Hours offHours;

    public OffHourLogProcessor(OnCloseProcessor processor, Hours offhours) {
        super(processor);
        this.offHours = offhours;
    }

    public String getFileName() {
        return "C:\\workspace\\eBaby\\log\\offhour-transaction.log";
    }

    public void process(Auction auction) {
        String message = String.format("itemName:%s seller:%s bidder:%s bidPrice:%s",
                auction.itemName,
                auction.seller.userName,
                auction.highestBidder != null ? auction.highestBidder.userName : "---",
                auction.highestPrice);

        if (this.offHours.isOffHours()) {
            AuctionLogger.getInstance().log(getFileName(), message);
        }
        super.process(auction);
    }


}
