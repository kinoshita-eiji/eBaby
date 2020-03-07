package application.fee;

import java.math.BigDecimal;

import application.Auction;

public class DownloadSoftwareShippingFee extends ShippingFee {

    public DownloadSoftwareShippingFee(Auction auction) {
        super(auction);
    }

    @Override
    public BigDecimal calculate() {
        return new BigDecimal(0);
    }

}
