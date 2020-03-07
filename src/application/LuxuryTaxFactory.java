package application;

public class LuxuryTaxFactory {

    public static LuxuryTax getInstance(Auction auction) {
        if (auction.itemCategory == ItemCategory.CAR) {
            return new CarLuxuryTax(auction);
        } else {
            return new LuxuryTax(auction);
        }
    }

}
