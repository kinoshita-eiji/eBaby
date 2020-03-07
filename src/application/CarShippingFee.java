package application;

import java.math.BigDecimal;

public class CarShippingFee extends ShippingFee {

    int defaultShippingFee = 1000;
    int preferredShippingFee = 500;

    public CarShippingFee(Auction auction) {
        super(auction);
    }

    @Override
    public BigDecimal calculate() {
        if (auction.isPreferred()) {
            return new BigDecimal(preferredShippingFee);
        } else {
            return new BigDecimal(defaultShippingFee);
        }
    }

}
