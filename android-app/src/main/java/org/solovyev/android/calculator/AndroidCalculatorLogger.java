package org.solovyev.android.calculator;

import android.util.Log;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 10/11/12
 * Time: 12:15 AM
 */
public class AndroidCalculatorLogger implements CalculatorLogger {

	@Nonnull
	private static final String TAG = "Calculatorpp";

	@Override
	public void debug(@Nullable String tag, @Nonnull String message) {
		Log.d(getTag(tag), message);
	}

	@Nonnull
	private String getTag(@Nullable String tag) {
		return tag != null ? TAG + "/" + tag : TAG;
	}

	@Override
	public void debug(@Nullable String tag, @Nullable String message, @Nonnull Throwable e) {
		Log.d(getTag(tag), message, e);
	}

	@Override
	public void error(@Nullable String tag, @Nullable String message, @Nonnull Throwable e) {
		Log.e(getTag(tag), message, e);
	}

	@Override
	public void error(@Nullable String tag, @Nullable String message) {
		Log.e(getTag(tag), message);
	}
}
