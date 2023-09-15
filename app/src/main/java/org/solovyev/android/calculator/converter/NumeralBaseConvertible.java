package org.solovyev.android.calculator.converter;

import android.content.Context;
import androidx.annotation.NonNull;

import org.solovyev.android.calculator.Named;

import jscl.JsclMathEngine;
import jscl.NumeralBase;
import midpcalc.Real;

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
    public String convert(@NonNull Convertible to, @NonNull String value) throws NumberFormatException {
        final NumeralBase baseTo = ((NumeralBaseConvertible) to).base;
        final Real real = Converter.parse(value, base.radix);
        if (real.isIntegral()) {
            final long l = real.toLong();
            if (l != Long.MAX_VALUE && l != -Long.MAX_VALUE) {
                return mathEngine.format(BigInteger.valueOf(l), baseTo);
            }
        }
        return mathEngine.format(real.toDouble(), baseTo);
    }

    @NonNull
    @Override
    public Named<Convertible> named(@NonNull Context context) {
        return Named.<Convertible>create(this, base.name());
    }
}
