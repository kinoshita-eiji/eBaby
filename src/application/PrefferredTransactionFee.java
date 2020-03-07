package application;

public class PrefferredTransactionFee extends TransactionFee {

    public PrefferredTransactionFee(Auction auction) {
        super(auction);
    }

    protected double getRate() {
        return 0.01;
    }
}
