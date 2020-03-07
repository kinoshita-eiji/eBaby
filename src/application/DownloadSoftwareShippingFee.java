package application;

import java.math.BigDecimal;

public class DownloadSoftwareShippingFee extends ShippingFee {

    public DownloadSoftwareShippingFee(Auction auction) {
        super(auction);
    }

    @Override
    public BigDecimal calculate() {
        return new BigDecimal(0);
    }

}
