package org.solovyev.android.calculator;

import android.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 10/11/12
 * Time: 12:15 AM
 */
public class AndroidCalculatorLogger implements CalculatorLogger {

    @NotNull
    private static final String TAG = AndroidCalculatorLogger.class.getSimpleName();

    @Override
    public void debug(@Nullable String tag, @NotNull String message) {
        Log.d(getTag(tag), message);
    }

    @NotNull
    private String getTag(@Nullable String tag) {
        return tag != null ? tag : TAG;
    }

    @Override
    public void debug(@Nullable String tag, @NotNull String message, @NotNull Throwable e) {
        Log.d(getTag(tag), message, e);
    }
}
