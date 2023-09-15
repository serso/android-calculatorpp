package org.solovyev.android.calculator.converter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import org.solovyev.android.calculator.Named;
import org.solovyev.android.calculator.R;

import javax.measure.unit.Dimension;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.util.*;

enum UnitDimension implements ConvertibleDimension {
    TIME(Dimension.TIME, R.string.cpp_converter_time),
    AMOUNT_OF_SUBSTANCE(Dimension.AMOUNT_OF_SUBSTANCE, R.string.cpp_converter_amount_of_substance),
    ELECTRIC_CURRENT(Dimension.ELECTRIC_CURRENT, R.string.cpp_converter_electric_current),
    LENGTH(Dimension.LENGTH, R.string.cpp_converter_length),
    MASS(Dimension.MASS, R.string.cpp_converter_mass),
    TEMPERATURE(Dimension.TEMPERATURE, R.string.cpp_converter_temperature);

    // todo serso: better to provide a dimension-id pair as units might not be unique in different dimensions
    @NonNull
    private static final Set<String> excludedUnits = new HashSet<>(Arrays.asList("year_sidereal", "year_calendar", "day_sidereal", "foot_survey_us", "me", "u"));
    @NonNull
    private static final Map<Dimension, List<Convertible>> units = new HashMap<>();

    static {
        for (Unit<?> unit : SI.getInstance().getUnits()) {
            addUnit(unit);
        }
        for (Unit<?> unit : NonSI.getInstance().getUnits()) {
            addUnit(unit);
        }
    }

    @NonNull
    public final Dimension dimension;
    @StringRes
    public final int name;
    UnitDimension(@NonNull Dimension dimension, @StringRes int name) {
        this.dimension = dimension;
        this.name = name;
    }

    private static void addUnit(@NonNull Unit<?> unit) {
        if (excludedUnits.contains(unit.toString())) {
            return;
        }

        final Dimension dimension = unit.getDimension();
        List<Convertible> unitsInDimension = units.get(dimension);
        if (unitsInDimension == null) {
            unitsInDimension = new ArrayList<>();
            units.put(dimension, unitsInDimension);
        }
        unitsInDimension.add(UnitConvertible.create(unit));
    }

    @Nullable
    public static UnitDimension of(@NonNull Unit<?> unit) {
        for (UnitDimension myDimension : values()) {
            if (myDimension.dimension.equals(unit.getDimension())) {
                return myDimension;
            }
        }
        return null;
    }


    @NonNull
    @Override
    public Named<ConvertibleDimension> named(@NonNull Context context) {
        return Named.<ConvertibleDimension>create(this, name, context);
    }

    @NonNull
    @Override
    public List<Convertible> getUnits() {
        return units.get(dimension);
    }
}
