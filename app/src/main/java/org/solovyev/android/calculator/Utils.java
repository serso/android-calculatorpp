package org.solovyev.android.calculator;

import androidx.annotation.NonNull;

import javax.annotation.Nonnull;

public class Utils {
    @NonNull
    public static String getErrorMessage(@Nonnull Throwable error) {
        final String localizedMessage = error.getLocalizedMessage();
        return localizedMessage == null ? error.getClass().getSimpleName() : localizedMessage;
    }
}
