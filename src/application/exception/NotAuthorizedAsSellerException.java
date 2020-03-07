package application.exception;

public class NotAuthorizedAsSellerException extends RuntimeException {

    public NotAuthorizedAsSellerException(String message) {
        super(message);
    }

}
