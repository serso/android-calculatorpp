package jscl.math;

import jscl.AbstractJsclArithmeticException;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;

public class NotIntegrableException extends AbstractJsclArithmeticException {

    public NotIntegrableException(@Nonnull String messageCode, Object... parameters) {
        super(messageCode, parameters);
    }

    public NotIntegrableException(@Nonnull Expression e) {
        this(Messages.msg_21, e.toString());
    }

    public NotIntegrableException(@Nonnull Variable v) {
        this(Messages.msg_21, v.getName());
    }

    public NotIntegrableException() {
        this(Messages.msg_22);
    }
}
