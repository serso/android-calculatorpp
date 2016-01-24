package jscl.text;

import jscl.text.msg.JsclMessage;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageLevel;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

public class ParseException extends Exception implements Message {

    @Nonnull
    private final Message message;

    private final int position;

    @Nonnull
    private final String expression;

    public ParseException(@Nonnull String messageCode, int position, @Nonnull String expression, Object... parameters) {
        this.message = new JsclMessage(messageCode, MessageType.error, parameters);
        this.position = position;
        this.expression = expression;
    }

    public ParseException(@Nonnull Message message, int position, @Nonnull String expression) {
        this.message = message;
        this.position = position;
        this.expression = expression;
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
    @Override
    public MessageLevel getMessageLevel() {
        return this.message.getMessageLevel();
    }

    @Nonnull
    @Override
    public String getLocalizedMessage(@Nonnull Locale locale) {
        return this.message.getLocalizedMessage(locale);
    }

    public int getPosition() {
        return position;
    }

    @Nonnull
    public String getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParseException that = (ParseException) o;

        if (position != that.position) return false;
        if (!expression.equals(that.expression)) return false;
        if (!message.equals(that.message)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + position;
        result = 31 * result + expression.hashCode();
        return result;
    }
}
