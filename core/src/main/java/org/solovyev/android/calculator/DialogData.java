package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.common.msg.MessageLevel;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 12:47 PM
 */
public interface DialogData {

	@Nonnull
	String getMessage();

	@Nonnull
	MessageLevel getMessageLevel();

	@Nullable
	String getTitle();
}
