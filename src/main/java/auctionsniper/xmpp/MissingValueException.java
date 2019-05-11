package auctionsniper.xmpp;

public class MissingValueException extends RuntimeException {
    public MissingValueException(String fieldName) {
        super("Field '" + fieldName + "' not available");
    }
}
