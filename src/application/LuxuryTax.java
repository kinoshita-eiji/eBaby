package application;

import java.math.BigDecimal;

public class LuxuryTax {
    Auction auction;

    public LuxuryTax(Auction auction) {
        this.auction = auction;
    }

    public BigDecimal calculate() {
        return new BigDecimal(0);
    }
}
