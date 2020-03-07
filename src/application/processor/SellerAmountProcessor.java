package application.processor;

import java.math.BigDecimal;

import application.Auction;
import application.fee.PrefferredTransactionFee;
import application.fee.TransactionFee;

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
        auction.setSellerAmount(new BigDecimal(auction.getHighestPrice()).subtract(fee.calculate()));
        super.process(auction);
    }

}
