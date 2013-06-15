package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.common.msg.MessageType;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 1:01 PM
 */
public class StringDialogData implements DialogData {

	@Nonnull
	private final String message;

	@Nonnull
	private final MessageType messageType;

	@Nullable
	private final String title;

	private StringDialogData(@Nonnull String message, @Nonnull MessageType messageType, @Nullable String title) {
		this.message = message;
		this.messageType = messageType;
		this.title = title;
	}

	@Nonnull
	public static DialogData newInstance(@Nonnull String message, @Nonnull MessageType messageType, @Nullable String title) {
		return new StringDialogData(message, messageType, title);
	}

	@Nonnull
	@Override
	public String getMessage() {
		return message;
	}

	@Nonnull
	@Override
	public MessageType getMessageLevel() {
		return messageType;
	}

	@Nullable
	@Override
	public String getTitle() {
		return title;
	}
}
