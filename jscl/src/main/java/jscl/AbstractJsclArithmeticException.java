package jscl;

import jscl.text.msg.JsclMessage;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageLevel;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

public abstract class AbstractJsclArithmeticException extends ArithmeticException implements Message {

    @Nonnull
    private final Message message;

    public AbstractJsclArithmeticException(@Nonnull String messageCode, Object... parameters) {
        this.message = new JsclMessage(messageCode, MessageType.error, parameters);
    }

    public AbstractJsclArithmeticException(@Nonnull Message message) {
        this.message = message;
    }

    @Nonnull
    public String getMessageCode() {
        return this.message.getMessageCode();
    }

    @Nonnull
    public List<Object> getParameters() {
        return this.message.getParameters();
    }

    @Nonnull
    public MessageLevel getMessageLevel() {
        return this.message.getMessageLevel();
    }

    @Nonnull
    public String getLocalizedMessage(@Nonnull Locale locale) {
        return this.message.getLocalizedMessage(locale);
    }

    @Nonnull
    @Override
    public String getLocalizedMessage() {
        return this.getLocalizedMessage(Locale.getDefault());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractJsclArithmeticException that = (AbstractJsclArithmeticException) o;

        if (!message.equals(that.message)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }
}
