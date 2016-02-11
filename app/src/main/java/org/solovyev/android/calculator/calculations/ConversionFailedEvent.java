package org.solovyev.android.calculator.calculations;

import android.support.annotation.NonNull;

import org.solovyev.android.calculator.DisplayState;

public class ConversionFailedEvent extends BaseConversionEvent {

    public ConversionFailedEvent(@NonNull DisplayState state) {
        super(state);
    }
}
