package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 1:34 PM
 */
public interface CalculatorClipboard {

	@Nullable
	String getText();

	void setText(@Nonnull String text);

	void setText(@Nonnull CharSequence text);
}
