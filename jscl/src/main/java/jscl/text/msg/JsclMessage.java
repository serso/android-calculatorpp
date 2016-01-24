package jscl.text.msg;

import org.solovyev.common.msg.AbstractMessage;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: serso
 * Date: 11/26/11
 * Time: 11:20 AM
 */
public class JsclMessage extends AbstractMessage {

    public JsclMessage(@Nonnull String messageCode,
                       @Nonnull MessageType messageType,
                       @javax.annotation.Nullable Object... parameters) {
        super(messageCode, messageType, parameters);
    }

    public JsclMessage(@Nonnull String messageCode,
                       @Nonnull MessageType messageType,
                       @Nonnull List<?> parameters) {
        super(messageCode, messageType, parameters);
    }

    @Override
    protected String getMessagePattern(@Nonnull Locale locale) {
        final ResourceBundle rb = ResourceBundle.getBundle("jscl/text/msg/messages", locale);
        return rb.getString(getMessageCode());
    }
}
