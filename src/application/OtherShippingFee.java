package application;

import java.math.BigDecimal;

public class OtherShippingFee extends ShippingFee {

    public OtherShippingFee(Auction auction) {
        super(auction);
    }

    public boolean isFree() {
        return auction.isPreferred() && 50 <= auction.getHighestPrice();
    }

    @Override
    BigDecimal calculate() {
        return new BigDecimal(isFree() ? 0 : 10);
    }

}
