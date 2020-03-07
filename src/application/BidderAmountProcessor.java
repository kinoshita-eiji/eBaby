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
            return new DownloadSoftwareShippingFee(auction).calculate();
        }
        if (auction.itemCategory == ItemCategory.CAR) {
            return new CarShippingFee(auction).calculate();
        }
        return new OtherShippingFee(auction).calculate();
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
