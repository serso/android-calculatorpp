package jscl;

import jscl.text.msg.Messages;

import javax.annotation.Nonnull;

public class NumeralBaseException extends AbstractJsclArithmeticException {

    public NumeralBaseException(@Nonnull Double value) {
        super(Messages.msg_17, value);
    }
}
