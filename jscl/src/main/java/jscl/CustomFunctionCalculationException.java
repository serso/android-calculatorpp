package jscl;

import jscl.math.function.CustomFunction;
import jscl.text.msg.Messages;
import org.solovyev.common.msg.Message;

import javax.annotation.Nonnull;

public class CustomFunctionCalculationException extends JsclArithmeticException {

    @Nonnull
    private final Message causeMessage;

    public CustomFunctionCalculationException(@Nonnull CustomFunction function, @Nonnull Message causeMessage) {
        super(Messages.msg_19, function.getName(), causeMessage);
        this.causeMessage = causeMessage;
    }

    @Nonnull
    public Message getCauseMessage() {
        return causeMessage;
    }
}
