package application;

public class ShippingFeeFactory {

    public static ShippingFee getInstance(Auction auction) {
        switch (auction.itemCategory) {
        case DOWNLOAD_SOFTWARE:
            return new DownloadSoftwareShippingFee(auction);
        case CAR:
            return new CarShippingFee(auction);
        default:
            return new OtherShippingFee(auction);
        }
    }

}
