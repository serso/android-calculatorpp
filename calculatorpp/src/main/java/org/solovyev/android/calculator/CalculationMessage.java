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
public class CalculationMessage implements Parcelable {

    public static final Creator<CalculationMessage> CREATOR = new Creator<CalculationMessage>() {
        @Override
        public CalculationMessage createFromParcel(@NotNull Parcel in) {
            return CalculationMessage.fromParcel(in);
        }

        @Override
        public CalculationMessage[] newArray(int size) {
            return new CalculationMessage[size];
        }
    };

    @NotNull
    private static CalculationMessage fromParcel(@NotNull Parcel in) {
        final String message = in.readString();
        final MessageType messageType = (MessageType) in.readSerializable();
        final CalculatorFixableError fixableError = (CalculatorFixableError) in.readSerializable();

        return new CalculationMessage(message, messageType, fixableError);
    }

    @NotNull
    private final String message;

    @NotNull
    private final MessageType messageType;

    @Nullable
    private final CalculatorFixableError fixableError;

    public CalculationMessage(@NotNull Message message) {
        this.message = message.getLocalizedMessage();
        this.messageType = message.getMessageType();
        this.fixableError = CalculatorFixableError.getErrorByMessageCode(message.getMessageCode());
    }

    public CalculationMessage(@NotNull String message,
                              @NotNull MessageType messageType,
                              @Nullable CalculatorFixableError fixableError) {
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
    public CalculatorFixableError getFixableError() {
        return fixableError;
    }
}
