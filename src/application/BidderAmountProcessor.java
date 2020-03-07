package application;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class BidderAmountProcessor extends AmountProcessor {

    public BidderAmountProcessor(OnCloseProcessor processor) {
        super(processor);
    }

    public void process(Auction auction) {
        auction.bidderAmount = new BigDecimal(auction.highestPrice)
                .add(calculateShippingFee(auction))
                .add(calculateLuxuryTax(auction));
        super.process(auction);
    }

    // TODO メソッドの実装が酷い・・・
    private BigDecimal calculateShippingFee(Auction auction) {
        if (auction.itemCategory == ItemCategory.DOWNLOAD_SOFTWARE) {
            return new BigDecimal(0);
        } else if (auction.itemCategory == ItemCategory.CAR) {
            if (auction.seller.isPreferredSeller) {
                return new BigDecimal(500);
            } else {
                return new BigDecimal(1000);
            }
        } else if (auction.seller.isPreferredSeller && auction.highestPrice >= 50) {
            return new BigDecimal(0);
        } else {
            return new BigDecimal(10);
        }
    }

    private BigDecimal calculateLuxuryTax(Auction auction) {
        if (auction.itemCategory != ItemCategory.CAR) {
            return new BigDecimal(0);
        }
        if (auction.highestPrice >= 50000) {
            return new BigDecimal(auction.highestPrice * 0.04).setScale(0, RoundingMode.DOWN);
        } else {
            return new BigDecimal(0);
        }
    }

}
