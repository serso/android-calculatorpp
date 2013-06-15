package org.solovyev.android.calculator;

import android.os.Parcel;
import android.os.Parcelable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
		public FixableMessage createFromParcel(@Nonnull Parcel in) {
			return FixableMessage.fromParcel(in);
		}

		@Override
		public FixableMessage[] newArray(int size) {
			return new FixableMessage[size];
		}
	};

	@Nonnull
	private static FixableMessage fromParcel(@Nonnull Parcel in) {
		final String message = in.readString();
		final MessageType messageType = (MessageType) in.readSerializable();
		final FixableError fixableError = (FixableError) in.readSerializable();

		return new FixableMessage(message, messageType, fixableError);
	}

	@Nonnull
	private final String message;

	@Nonnull
	private final MessageType messageType;

	@Nullable
	private final FixableError fixableError;

	public FixableMessage(@Nonnull Message message) {
		this.message = message.getLocalizedMessage();
		int messageLevel = message.getMessageLevel().getMessageLevel();
		if (messageLevel < MessageType.info.getMessageLevel()) {
			this.messageType = MessageType.info;
		} else if (messageLevel < MessageType.warning.getMessageLevel()) {
			this.messageType = MessageType.warning;
		} else {
			this.messageType = MessageType.error;
		}
		this.fixableError = CalculatorFixableError.getErrorByMessageCode(message.getMessageCode());
	}

	public FixableMessage(@Nonnull String message,
						  @Nonnull MessageType messageType,
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
	public void writeToParcel(@Nonnull Parcel out, int flags) {
		out.writeString(message);
		out.writeSerializable(messageType);
		out.writeSerializable(fixableError);
	}

	@Nonnull
	public String getMessage() {
		return message;
	}

	@Nonnull
	public MessageType getMessageType() {
		return messageType;
	}

	@Nullable
	public FixableError getFixableError() {
		return fixableError;
	}
}
