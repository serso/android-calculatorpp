package org.solovyev.android.calculator.converter;

import android.content.Context;
import android.support.annotation.NonNull;
import jscl.NumeralBase;
import org.solovyev.android.calculator.R;

import java.util.ArrayList;
import java.util.List;

public class NumeralBaseDimension implements ConvertibleDimension {

    @NonNull
    private static final NumeralBaseDimension INSTANCE = new NumeralBaseDimension();
    @NonNull
    private final List<Convertible> units = new ArrayList<>();

    {
        for (NumeralBase base : NumeralBase.values()) {
            units.add(new NumeralBaseConvertible(base));
        }
    }

    private NumeralBaseDimension() {
    }

    @NonNull
    public static ConvertibleDimension get() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public Named<ConvertibleDimension> named(@NonNull Context context) {
        return Named.<ConvertibleDimension>create(this, R.string.cpp_radix, context);
    }

    @NonNull
    @Override
    public List<Convertible> getUnits() {
        return units;
    }
}
