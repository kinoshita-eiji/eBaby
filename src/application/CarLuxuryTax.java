package application;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CarLuxuryTax extends LuxuryTax {

    public CarLuxuryTax(Auction auction) {
        super(auction);
    }

    public BigDecimal calculate() {
        if (auction.getHighestPrice() >= 50000) {
            return new BigDecimal(auction.getHighestPrice() * 0.04).setScale(0, RoundingMode.DOWN);
        } else {
            return new BigDecimal(0);
        }
    }
}
