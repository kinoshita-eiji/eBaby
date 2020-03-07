package application.fee;

import java.math.BigDecimal;

import application.Auction;

public class LuxuryTax {
    Auction auction;

    public LuxuryTax(Auction auction) {
        this.auction = auction;
    }

    public BigDecimal calculate() {
        return new BigDecimal(0);
    }
}
