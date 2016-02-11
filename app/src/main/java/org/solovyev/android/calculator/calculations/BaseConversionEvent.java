package org.solovyev.android.calculator.calculations;

import android.support.annotation.NonNull;

import org.solovyev.android.calculator.DisplayState;

public class BaseConversionEvent {
    @NonNull
    public final DisplayState state;

    public BaseConversionEvent(@NonNull DisplayState state) {
        this.state = state;
    }
}
