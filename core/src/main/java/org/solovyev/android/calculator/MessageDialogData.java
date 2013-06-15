package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageLevel;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 12:45 PM
 */
public class MessageDialogData implements DialogData {

	@Nonnull
	private Message message;

	@Nullable
	private String title;

	private MessageDialogData(@Nonnull Message message, @Nullable String title) {
		this.message = message;
		this.title = title;
	}

	@Nonnull
	public static DialogData newInstance(@Nonnull Message message, @Nullable String title) {
		return new MessageDialogData(message, title);
	}

	@Override
	@Nonnull
	public String getMessage() {
		return message.getLocalizedMessage();
	}

	@Nonnull
	@Override
	public MessageLevel getMessageLevel() {
		return message.getMessageLevel();
	}

	@Override
	@Nullable
	public String getTitle() {
		return title;
	}
}
