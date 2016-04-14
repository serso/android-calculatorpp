package org.solovyev.android.calculator.converter;

import android.content.Context;
import android.support.annotation.NonNull;
import jscl.NumeralBase;

public class NumeralBaseConvertible implements Convertible {

    @NonNull
    private final NumeralBase base;

    public NumeralBaseConvertible(@NonNull NumeralBase base) {
        this.base = base;
    }

    @NonNull
    @Override
    public String convert(@NonNull Convertible to, @NonNull String value) {
        try {
            base.toBigInteger(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    @Override
    public Named<Convertible> named(@NonNull Context context) {
        return Named.<Convertible>create(this, base.name());
    }
}
