package application.exception;

public class InvalidAuctionTimeException extends RuntimeException {

    public InvalidAuctionTimeException(String message) {
        super(message);
    }

}
