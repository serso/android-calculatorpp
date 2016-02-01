package jscl;

import javax.annotation.Nonnull;

public class NotSupportedException extends JsclArithmeticException {

    public NotSupportedException(@Nonnull String messageCode, Object... parameters) {
        super(messageCode, parameters);
    }
}
