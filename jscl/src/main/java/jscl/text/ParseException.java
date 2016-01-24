package jscl.text;

import org.solovyev.common.collections.Collections;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageLevel;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ParseException extends Exception implements Message {

    private int position;
    @Nonnull
    private String expression;
    @Nonnull
    private String messageCode;
    @Nonnull
    private List<?> parameters;

    ParseException() {
    }

    public ParseException(int position, @Nonnull String expression, @Nonnull String messageCode, @Nullable Object... parameters) {
        set(position, expression, messageCode, Collections.asList(parameters));
    }

    void set(int position, @Nonnull String expression, @Nonnull String messageCode, @Nonnull List<?> parameters) {
        this.position = position;
        this.expression = expression;
        this.messageCode = messageCode;
        this.parameters = parameters;
    }

    @Nonnull
    public String getMessageCode() {
        return messageCode;
    }

    @Nonnull
    public List<Object> getParameters() {
        return (List<Object>) parameters;
    }

    @Nonnull
    @Override
    public MessageLevel getMessageLevel() {
        return MessageType.error;
    }

    @Nonnull
    @Override
    public String getLocalizedMessage(@Nonnull Locale locale) {
        final ResourceBundle rb = ResourceBundle.getBundle("jscl/text/msg/messages", locale);
        return rb.getString(getMessageCode());
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

        ParseException exception = (ParseException) o;

        if (position != exception.position) return false;
        if (!expression.equals(exception.expression)) return false;
        if (!messageCode.equals(exception.messageCode)) return false;
        return parameters.equals(exception.parameters);

    }

    @Override
    public int hashCode() {
        int result = position;
        result = 31 * result + expression.hashCode();
        result = 31 * result + messageCode.hashCode();
        result = 31 * result + parameters.hashCode();
        return result;
    }
}
