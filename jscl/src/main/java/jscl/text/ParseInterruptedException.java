package jscl.text;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 11:39 PM
 */
public class ParseInterruptedException extends RuntimeException {

    public ParseInterruptedException() {
    }

    public ParseInterruptedException(String message) {
        super(message);
    }

    public ParseInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseInterruptedException(Throwable cause) {
        super(cause);
    }
}
