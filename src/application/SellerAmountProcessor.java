package application;

import java.math.BigDecimal;

public class SellerAmountProcessor extends AmountProcessor {

    public SellerAmountProcessor(OnCloseProcessor processor) {
        super(processor);
    }

    public void process(Auction auction) {
        TransactionFee fee;
        if (auction.isPreferred()) {
            fee = new PrefferredTransactionFee(auction);
        } else {
            fee = new TransactionFee(auction);
        }
        auction.sellerAmount = new BigDecimal(auction.getHighestPrice()).subtract(fee.calculate());
        super.process(auction);
    }

}
