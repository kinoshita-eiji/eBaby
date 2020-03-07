package application;

import java.math.BigDecimal;

public class BidderAmountProcessor extends AmountProcessor {

    public BidderAmountProcessor(OnCloseProcessor processor) {
        super(processor);
    }

    public void process(Auction auction) {
        auction.bidderAmount = new BigDecimal(auction.getHighestPrice())
                .add(ShippingFeeFactory.getInstance(auction).calculate())
                .add(LuxuryTaxFactory.getInstance(auction).calculate());
        super.process(auction);
    }

}
