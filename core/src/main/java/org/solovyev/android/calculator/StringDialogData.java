package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.msg.MessageType;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 1:01 PM
 */
public class StringDialogData implements DialogData {

	@NotNull
	private final String message;

	@NotNull
	private final MessageType messageType;

	@Nullable
	private final String title;

	private StringDialogData(@NotNull String message, @NotNull MessageType messageType, @Nullable String title) {
		this.message = message;
		this.messageType = messageType;
		this.title = title;
	}

	@NotNull
	public static DialogData newInstance(@NotNull String message, @NotNull MessageType messageType, @Nullable String title) {
		return new StringDialogData(message, messageType, title);
	}

	@NotNull
	@Override
	public String getMessage() {
		return message;
	}

	@NotNull
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
