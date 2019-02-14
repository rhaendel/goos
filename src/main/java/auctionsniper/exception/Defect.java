package auctionsniper.exception;

/**
 * Throw this Exception when the code reaches a condition that could only be
 * caused by a programming error, rather than a failure in the runtime
 * environment.<br>
 *
 * Its name could also have been "StupidProgrammerMistakeException".
 *
 */
public class Defect extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public Defect(String message) {
        super(message);
    }

    public Defect(String message, Throwable cause) {
        super(message, cause);
    }

}
