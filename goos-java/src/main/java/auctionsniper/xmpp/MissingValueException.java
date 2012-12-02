package auctionsniper.xmpp;

public class MissingValueException extends Exception {
    public MissingValueException(String fieldName) {
        super("missing value for " + fieldName);
    }
}
