package application;

import java.math.BigDecimal;

public class OtherShippingFee extends ShippingFee {

    public OtherShippingFee(Auction auction) {
        super(auction);
    }

    @Override
    BigDecimal calculate() {
        if (auction.seller.isPreferredSeller && auction.highestPrice >= 50) {
            return new BigDecimal(0);
        }
        return new BigDecimal(10);
    }

}
