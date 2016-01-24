package jscl;

import javax.annotation.Nonnull;

public class NotSupportedException extends AbstractJsclArithmeticException {

    public NotSupportedException(@Nonnull String messageCode, Object... parameters) {
        super(messageCode, parameters);
    }
}
