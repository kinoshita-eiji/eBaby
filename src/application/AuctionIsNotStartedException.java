package application;

public class AuctionIsNotStartedException extends RuntimeException {

    public AuctionIsNotStartedException(String message) {
        super(message);
    }

}
