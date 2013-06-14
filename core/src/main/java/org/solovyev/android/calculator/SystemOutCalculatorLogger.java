package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 10/11/12
 * Time: 12:12 AM
 */
public class SystemOutCalculatorLogger implements CalculatorLogger {

	@NotNull
	private static final String TAG = SystemOutCalculatorLogger.class.getSimpleName();

	@Override
	public void debug(@Nullable String tag, @Nullable String message) {
		System.out.println(getTag(tag) + ": " + message);
	}

	@NotNull
	private String getTag(@Nullable String tag) {
		return tag != null ? tag : TAG;
	}

	@Override
	public void debug(@Nullable String tag, @Nullable String message, @NotNull Throwable e) {
		debug(tag, message);
		e.printStackTrace(System.out);
	}

	@Override
	public void error(@Nullable String tag, @Nullable String message, @NotNull Throwable e) {
		System.out.println(getTag(tag) + ": " + message);
		e.printStackTrace(System.out);
	}

	@Override
	public void error(@Nullable String tag, @Nullable String message) {
		System.out.println(getTag(tag) + ": " + message);
	}
}
