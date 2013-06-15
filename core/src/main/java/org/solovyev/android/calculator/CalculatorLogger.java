package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 10/11/12
 * Time: 12:11 AM
 */
public interface CalculatorLogger {

	void debug(@Nullable String tag, @Nonnull String message);

	void debug(@Nullable String tag, @Nullable String message, @Nonnull Throwable e);

	void error(@Nullable String tag, @Nullable String message, @Nonnull Throwable e);

	void error(@Nullable String tag, @Nullable String message);

}
