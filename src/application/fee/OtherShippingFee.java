package application.fee;

import java.math.BigDecimal;

import application.Auction;

public class OtherShippingFee extends ShippingFee {

    public OtherShippingFee(Auction auction) {
        super(auction);
    }

    public boolean isFree() {
        return auction.isPreferred() && 50 <= auction.getHighestPrice();
    }

    @Override
    public BigDecimal calculate() {
        return new BigDecimal(isFree() ? 0 : 10);
    }

}
