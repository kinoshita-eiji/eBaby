package application.fee;

import application.Auction;
import application.ItemCategory;

public class LuxuryTaxFactory {

    public static LuxuryTax getInstance(Auction auction) {
        if (auction.getItemCategory() == ItemCategory.CAR) {
            return new CarLuxuryTax(auction);
        } else {
            return new LuxuryTax(auction);
        }
    }

}
