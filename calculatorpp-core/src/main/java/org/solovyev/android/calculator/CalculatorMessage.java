package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.solovyev.common.msg.AbstractMessage;
import org.solovyev.common.msg.MessageType;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 8:06 PM
 */
public class CalculatorMessage extends AbstractMessage {

    protected CalculatorMessage(@NotNull String messageCode, @NotNull MessageType messageType, @org.jetbrains.annotations.Nullable Object... parameters) {
        super(messageCode, messageType, parameters);
    }

    protected CalculatorMessage(@NotNull String messageCode, @NotNull MessageType messageType, @NotNull List<?> parameters) {
        super(messageCode, messageType, parameters);
    }

    @Override
    protected String getMessagePattern(@NotNull Locale locale) {
        final ResourceBundle rb = CalculatorMessages.getBundle(locale);
        return rb.getString(getMessageCode());
    }
}
