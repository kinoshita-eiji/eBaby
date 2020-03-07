package application.fee;

import application.Auction;

public class ShippingFeeFactory {

    public static ShippingFee getInstance(Auction auction) {
        switch (auction.getItemCategory()) {
        case DOWNLOAD_SOFTWARE:
            return new DownloadSoftwareShippingFee(auction);
        case CAR:
            return new CarShippingFee(auction);
        default:
            return new OtherShippingFee(auction);
        }
    }

}
