package application;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CarLuxuryTax extends LuxuryTax {

    int taxedThreshold = 50000;
    double taxRate = 0.04;

    public int getTaxedThreshold() {
        return taxedThreshold;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public CarLuxuryTax(Auction auction) {
        super(auction);
    }

    public BigDecimal calculate() {
        if (getTaxedThreshold() <= auction.getHighestPrice()) {
            return new BigDecimal(auction.getHighestPrice() * getTaxRate()).setScale(0, RoundingMode.DOWN);
        } else {
            return new BigDecimal(0);
        }
    }
}
