package jscl.math;

import javax.annotation.Nonnull;

public class NotIntegerException extends ArithmeticException {

    @SuppressWarnings("ThrowableInstanceNeverThrown")
    private static final NotIntegerException INSTANCE = new NotIntegerException();

    private NotIntegerException() {
        super("Not integer!");
    }

    @Nonnull
    public static NotIntegerException get() {
        return INSTANCE;
    }
}
