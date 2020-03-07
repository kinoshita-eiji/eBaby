package application.fee;

import java.math.BigDecimal;

import application.Auction;

public abstract class ShippingFee {
    Auction auction;

    public ShippingFee(Auction auction) {
        this.auction = auction;
    }

    public abstract BigDecimal calculate();
}
