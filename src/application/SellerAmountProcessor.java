package application;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SellerAmountProcessor extends AmountProcessor {

    public SellerAmountProcessor(OnCloseProcessor processor) {
        super(processor);
    }

    public void process(Auction auction) {
        auction.sellerAmount = new BigDecimal(auction.highestPrice)
                .subtract(new BigDecimal(auction.highestPrice * 0.02).setScale(0, RoundingMode.DOWN));
        super.process(auction);
    }

}
