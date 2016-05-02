package org.solovyev.android.calculator.text;

import jscl.NumeralBase;

import javax.annotation.Nonnull;

public class NumberSpan {
    @Nonnull
    public final NumeralBase numeralBase;

    public NumberSpan(@Nonnull  NumeralBase numeralBase) {
        this.numeralBase = numeralBase;
    }
}
