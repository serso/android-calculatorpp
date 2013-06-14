package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 1:34 PM
 */
public interface CalculatorClipboard {

	@Nullable
	String getText();

	void setText(@NotNull String text);

	void setText(@NotNull CharSequence text);
}
