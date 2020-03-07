package application;

import java.math.BigDecimal;

public class CarShippingFee extends ShippingFee {

    public CarShippingFee(Auction auction) {
        super(auction);
    }

    @Override
    BigDecimal calculate() {
        if (auction.seller.isPreferredSeller()) {
            return new BigDecimal(500);
        } else {
            return new BigDecimal(1000);
        }
    }

}
