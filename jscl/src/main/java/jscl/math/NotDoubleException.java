package jscl.math;

import javax.annotation.Nonnull;

public class NotDoubleException extends ArithmeticException {

    @SuppressWarnings("ThrowableInstanceNeverThrown")
    private static final NotDoubleException INSTANCE = new NotDoubleException();

    private NotDoubleException() {
        super("Not double!");
    }

    @Nonnull
    public static NotDoubleException get() {
        return INSTANCE;
    }
}
