package org.solovyev.android.calculator.converter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import org.solovyev.android.calculator.R;

import javax.measure.unit.Dimension;
import javax.measure.unit.Unit;

enum NamedDimension {
    TIME(Dimension.TIME, R.string.cpp_converter_time),
    AMOUNT_OF_SUBSTANCE(Dimension.AMOUNT_OF_SUBSTANCE, R.string.cpp_converter_amount_of_substance),
    ELECTRIC_CURRENT(Dimension.ELECTRIC_CURRENT, R.string.cpp_converter_electric_current),
    LENGTH(Dimension.LENGTH, R.string.cpp_converter_length),
    MASS(Dimension.MASS, R.string.cpp_converter_mass),
    TEMPERATURE(Dimension.TEMPERATURE, R.string.cpp_converter_temperature);

    @NonNull
    public final Dimension dimension;
    @StringRes
    public final int name;

    NamedDimension(@NonNull Dimension dimension, @StringRes int name) {
        this.dimension = dimension;
        this.name = name;
    }

    @Nullable
    public static NamedDimension of(@NonNull Unit<?> unit) {
        for (NamedDimension myDimension : values()) {
            if (myDimension.dimension.equals(unit.getDimension())) {
                return myDimension;
            }
        }
        return null;
    }
}
