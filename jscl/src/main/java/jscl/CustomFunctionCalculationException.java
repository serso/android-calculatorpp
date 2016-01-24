package jscl;

import jscl.math.function.CustomFunction;
import jscl.text.msg.Messages;
import org.solovyev.common.msg.Message;

import javax.annotation.Nonnull;

public class CustomFunctionCalculationException extends AbstractJsclArithmeticException {

    public CustomFunctionCalculationException(@Nonnull CustomFunction function, @Nonnull Message message) {
        super(Messages.msg_19, function.getName(), message);
    }
}
