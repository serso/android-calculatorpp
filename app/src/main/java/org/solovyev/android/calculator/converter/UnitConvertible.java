package org.solovyev.android.calculator.converter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import jscl.JsclMathEngine;
import jscl.NumeralBase;
import midpcalc.Real;

import javax.annotation.Nonnull;
import javax.measure.unit.Unit;

final class UnitConvertible implements Convertible {
    @NonNull
    private final Unit unit;

    private UnitConvertible(@NonNull Unit unit) {
        this.unit = unit;
    }

    @NonNull
    static UnitConvertible create(@NonNull Unit unit) {
        return new UnitConvertible(unit);
    }

    @Override
    public String toString() {
        return unit.toString();
    }

    public double parse(@NonNull String value) {
        final String groupingSeparator = String.valueOf(JsclMathEngine.getInstance().getGroupingSeparator());
        if (!TextUtils.isEmpty(groupingSeparator)) {
            value = value.replace(groupingSeparator, "");
        }
        final long bits = new Real(value).toDoubleBits();
        return Double.longBitsToDouble(bits);
    }

    @NonNull
    public String format(double value) {
        return JsclMathEngine.getInstance().format(value, NumeralBase.dec);
    }

    @NonNull
    @Override
    public String convert(@NonNull Convertible to, @NonNull String value) {
        final double from = parse(value);
        final double converted = unit.getConverterTo(((UnitConvertible) to).unit).convert(from);
        return format(converted);
    }

    @NonNull
    @Override
    public Named<Convertible> named(@Nonnull Context context) {
        final UnitDimension dimension = UnitDimension.of(unit);
        if (dimension == null) {
            return Named.<Convertible>create(this, 0, context);
        }
        return Named.<Convertible>create(this, Converter.unitName(unit, dimension), context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UnitConvertible that = (UnitConvertible) o;
        return unit.equals(that.unit);

    }

    @Override
    public int hashCode() {
        return unit.hashCode();
    }
}
