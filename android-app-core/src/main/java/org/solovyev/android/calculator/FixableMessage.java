package org.solovyev.android.calculator;

import android.os.Parcel;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

/**
 * User: serso
 * Date: 11/17/12
 * Time: 6:54 PM
 */
public class FixableMessage implements Parcelable {

    public static final Creator<FixableMessage> CREATOR = new Creator<FixableMessage>() {
        @Override
        public FixableMessage createFromParcel(@NotNull Parcel in) {
            return FixableMessage.fromParcel(in);
        }

        @Override
        public FixableMessage[] newArray(int size) {
            return new FixableMessage[size];
        }
    };

    @NotNull
    private static FixableMessage fromParcel(@NotNull Parcel in) {
        final String message = in.readString();
        final MessageType messageType = (MessageType) in.readSerializable();
        final FixableError fixableError = (FixableError) in.readSerializable();

        return new FixableMessage(message, messageType, fixableError);
    }

    @NotNull
    private final String message;

    @NotNull
    private final MessageType messageType;

    @Nullable
    private final FixableError fixableError;

    public FixableMessage(@NotNull Message message) {
        this.message = message.getLocalizedMessage();
        this.messageType = message.getMessageType();
        this.fixableError = CalculatorFixableError.getErrorByMessageCode(message.getMessageCode());
    }

    public FixableMessage(@NotNull String message,
						  @NotNull MessageType messageType,
						  @Nullable FixableError fixableError) {
        this.message = message;
        this.messageType = messageType;
        this.fixableError = fixableError;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NotNull Parcel out, int flags) {
        out.writeString(message);
        out.writeSerializable(messageType);
        out.writeSerializable(fixableError);
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @NotNull
    public MessageType getMessageType() {
        return messageType;
    }

    @Nullable
    public FixableError getFixableError() {
        return fixableError;
    }
}
