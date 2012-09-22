package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.msg.AbstractMessage;
import org.solovyev.common.msg.Message;
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

    public CalculatorMessage(@NotNull String messageCode, @NotNull MessageType messageType, @Nullable Object... parameters) {
        super(messageCode, messageType, parameters);
    }

    public CalculatorMessage(@NotNull String messageCode, @NotNull MessageType messageType, @NotNull List<?> parameters) {
        super(messageCode, messageType, parameters);
    }

    @NotNull
    public static Message newInfoMessage(@NotNull String messageCode, @Nullable Object... parameters) {
        return new CalculatorMessage(messageCode, MessageType.info, parameters);
    }

    @NotNull
    public static Message newWarningMessage(@NotNull String messageCode, @Nullable Object... parameters) {
        return new CalculatorMessage(messageCode, MessageType.warning, parameters);
    }

    @NotNull
    public static Message newErrorMessage(@NotNull String messageCode, @Nullable Object... parameters) {
        return new CalculatorMessage(messageCode, MessageType.error, parameters);
    }

    @Override
    protected String getMessagePattern(@NotNull Locale locale) {
        final ResourceBundle rb = CalculatorMessages.getBundle(locale);
        return rb.getString(getMessageCode());
    }
}
