package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 10/11/12
 * Time: 12:11 AM
 */
public interface CalculatorLogger {

    void debug(@Nullable String tag, @NotNull String message);

    void debug(@Nullable String tag, @NotNull String message, @NotNull Throwable e);
}
