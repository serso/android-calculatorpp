package org.solovyev.android.calculator.calculations;

import android.support.annotation.NonNull;

import org.solovyev.android.calculator.DisplayState;

import jscl.NumeralBase;

public class ConversionFinishedEvent extends BaseConversionEvent {
    @NonNull
    public final String result;
    @NonNull
    public final NumeralBase numeralBase;

    public ConversionFinishedEvent(@NonNull String result, @NonNull NumeralBase numeralBase,
            @NonNull DisplayState state) {
        super(state);
        this.result = result;
        this.numeralBase = numeralBase;
    }
}
