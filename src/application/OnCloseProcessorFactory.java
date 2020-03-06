package application;

public class OnCloseProcessorFactory {

    public OnCloseProcessorFactory() {
    }

    public static OnCloseProcessor getProcessor(Auction auction) {
        if (auction.highestBidder == null) {
            return new SellerAmountProcessor(
                    new BidderAmountProcessor(
                            new CarTransactionLogProcessor(
                                    new ExpensiveTransactionLogProcessor(
                                            new WithoutBidAuctionCloseNotifier()))));

        } else {
            return new SellerAmountProcessor(
                    new BidderAmountProcessor(
                            new CarTransactionLogProcessor(
                                    new ExpensiveTransactionLogProcessor(
                                            new WithBidAuctionCloseNotifier()))));

        }
    }

}
