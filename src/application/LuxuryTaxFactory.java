package application;

public class LuxuryTaxFactory {

    public static LuxuryTax getInstance(Auction auction) {
        switch (auction.itemCategory) {
        case CAR:
            return new CarLuxuryTax(auction);
        default:
            return new LuxuryTax(auction);
        }
    }

}
