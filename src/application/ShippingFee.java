package application;

import java.math.BigDecimal;

public abstract class ShippingFee {
    Auction auction;

    public ShippingFee(Auction auction) {
        this.auction = auction;
    }

    public abstract BigDecimal calculate();
}
