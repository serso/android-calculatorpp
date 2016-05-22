package jscl;

import jscl.text.msg.JsclMessage;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageLevel;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

public abstract class JsclArithmeticException extends ArithmeticException implements Message {

    @Nonnull
    private Message message;

    public JsclArithmeticException(@Nonnull String messageCode, Object... parameters) {
        this.message = new JsclMessage(messageCode, MessageType.error, parameters);
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

        JsclArithmeticException that = (JsclArithmeticException) o;

        return message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }

    public void setMessage(@Nonnull Message message) {
        this.message = message;
    }
}
