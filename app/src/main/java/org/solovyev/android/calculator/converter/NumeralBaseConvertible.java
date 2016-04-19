package org.solovyev.android.calculator.converter;

import android.content.Context;
import android.support.annotation.NonNull;
import jscl.JsclMathEngine;
import jscl.NumeralBase;

import java.math.BigInteger;

public class NumeralBaseConvertible implements Convertible {

    @NonNull
    private final NumeralBase base;
    @NonNull
    private final JsclMathEngine mathEngine = JsclMathEngine.getInstance();

    public NumeralBaseConvertible(@NonNull NumeralBase base) {
        this.base = base;
    }

    @NonNull
    @Override
    public String convert(@NonNull Convertible to, @NonNull String value) {
        final NumeralBase baseTo = ((NumeralBaseConvertible) to).base;
        try {
            final BigInteger integer = base.toBigInteger(value);
            return mathEngine.format(integer, baseTo);
        } catch (NumberFormatException e) {
            final double d = Converter.parse(value, base.radix);
            return mathEngine.format(d, baseTo);
        }
    }

    @NonNull
    @Override
    public Named<Convertible> named(@NonNull Context context) {
        return Named.<Convertible>create(this, base.name());
    }
}
