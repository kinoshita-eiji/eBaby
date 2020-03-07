package application;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransactionFee {

    Auction auction;
    public TransactionFee(Auction auction) {
        this.auction = auction;
    }

    public BigDecimal calculate() {
        return new BigDecimal(auction.highestPrice * getRate()).setScale(0, RoundingMode.DOWN);
    }

    protected double getRate() {
        return 0.02;
    }
}
