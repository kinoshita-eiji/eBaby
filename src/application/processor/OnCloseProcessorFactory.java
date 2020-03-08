package application.processor;

import com.tobeagile.training.ebaby.services.Hours;
import com.tobeagile.training.ebaby.services.OffHours;

import application.Auction;

public class OnCloseProcessorFactory {

    public OnCloseProcessorFactory() {
    }

    public static OnCloseProcessor getProcessor(Auction auction, Hours offHours) {
        return new SellerAmountProcessor(
                new BidderAmountProcessor(
                        new CarTransactionLogProcessor(
                                new ExpensiveTransactionLogProcessor(
                                        new OffHourLogProcessor(
                                                new AuctionCloseSellerNotifier(
                                                        new AuctionCloseBidderNotifier(null)),
                                                offHours)))));
    }

    public static OnCloseProcessor getProcessor(Auction auction) {
        return getProcessor(auction, OffHours.getInstance());
    }

}
