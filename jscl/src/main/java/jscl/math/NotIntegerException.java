package jscl.math;

public class NotIntegerException extends ArithmeticException {
    public NotIntegerException() {
        this("Not integer!");
    }

    public NotIntegerException(String s) {
        super(s);
    }
}
