package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 12:45 PM
 */
public class MessageDialogData implements DialogData {

    @NotNull
    private Message message;

    @Nullable
    private String title;

    private MessageDialogData(@NotNull Message message, @Nullable String title) {
        this.message = message;
        this.title = title;
    }

    @NotNull
    public static DialogData newInstance(@NotNull Message message, @Nullable String title) {
        return new MessageDialogData(message, title);
    }

    @Override
    @NotNull
    public String getMessage() {
        return message.getLocalizedMessage();
    }

    @NotNull
    @Override
    public MessageType getMessageType() {
        return message.getMessageType();
    }

    @Override
    @Nullable
    public String getTitle() {
        return title;
    }
}
