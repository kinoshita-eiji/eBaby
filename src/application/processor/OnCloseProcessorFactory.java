package application.processor;

import com.tobeagile.training.ebaby.services.Hours;
import com.tobeagile.training.ebaby.services.OffHours;

import application.Auction;

public class OnCloseProcessorFactory {

    public OnCloseProcessorFactory() {
    }

    public static OnCloseProcessor getProcessor(Auction auction, Hours offHours) {
        if (!auction.hasBid()) {
            return new SellerAmountProcessor(
                    new BidderAmountProcessor(
                            new CarTransactionLogProcessor(
                                    new ExpensiveTransactionLogProcessor(
                                            new OffHourLogProcessor(
                                                    new WithoutBidAuctionCloseNotifier(),
                                                    offHours)))));

        } else {
            return new SellerAmountProcessor(
                    new BidderAmountProcessor(
                            new CarTransactionLogProcessor(
                                    new ExpensiveTransactionLogProcessor(
                                            new OffHourLogProcessor(
                                                    new WithBidAuctionCloseNotifier(),
                                                    offHours)))));
        }
    }

    public static OnCloseProcessor getProcessor(Auction auction) {
        return getProcessor(auction, OffHours.getInstance());
    }

}
