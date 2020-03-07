package application.processor;

import java.math.BigDecimal;

import application.Auction;
import application.LuxuryTaxFactory;
import application.ShippingFeeFactory;

public class BidderAmountProcessor extends AmountProcessor {

    public BidderAmountProcessor(OnCloseProcessor processor) {
        super(processor);
    }

    public void process(Auction auction) {
        auction.setBidderAmount(new BigDecimal(auction.getHighestPrice())
                .add(ShippingFeeFactory.getInstance(auction).calculate())
                .add(LuxuryTaxFactory.getInstance(auction).calculate()));
        super.process(auction);
    }

}
