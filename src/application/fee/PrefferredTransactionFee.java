package application.fee;

import application.Auction;

public class PrefferredTransactionFee extends TransactionFee {

    public PrefferredTransactionFee(Auction auction) {
        super(auction);
    }

    protected double getRate() {
        return 0.01;
    }
}
