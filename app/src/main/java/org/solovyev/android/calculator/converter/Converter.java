package org.solovyev.android.calculator.converter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.google.common.base.Strings;
import jscl.JsclMathEngine;
import midpcalc.Real;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import javax.measure.unit.Unit;

final class Converter {
    static int unitName(@Nonnull Unit unit, @Nonnull UnitDimension dimension) {
        final String id = Strings.nullToEmpty(unit.toString());
        switch (dimension) {
            case TIME:
                switch (id) {
                    case "s":
                        return R.string.cpp_units_time_seconds;
                    case "week":
                        return R.string.cpp_units_time_weeks;
                    case "day":
                        return R.string.cpp_units_time_days;
                    case "min":
                        return R.string.cpp_units_time_minutes;
                    case "year":
                        return R.string.cpp_units_time_years;
                    case "h":
                        return R.string.cpp_units_time_hours;
                    case "month":
                        return R.string.cpp_units_time_months;
                }
                break;
            case AMOUNT_OF_SUBSTANCE:
                switch (id) {
                    case "mol":
                        return R.string.cpp_units_aos_mol;
                    case "atom":
                        return R.string.cpp_units_aos_atoms;
                }
                break;
            case ELECTRIC_CURRENT:
                switch (id) {
                    case "A":
                        return R.string.cpp_units_ec_a;
                    case "Gi":
                        return R.string.cpp_units_ec_gi;
                }
                break;
            case LENGTH:
                switch (id) {
                    case "m":
                        return R.string.cpp_units_length_meters;
                    case "nmi":
                        return R.string.cpp_units_length_nautical_miles;
                    case "in":
                        return R.string.cpp_units_length_inches;
                    case "ua":
                        return R.string.cpp_units_length_astronomical_units;
                    case "Å":
                        return R.string.cpp_units_length_angstroms;
                    case "ly":
                        return R.string.cpp_units_length_light_years;
                    case "mi":
                        return R.string.cpp_units_length_miles;
                    case "yd":
                        return R.string.cpp_units_length_yards;
                    case "pixel":
                        return R.string.cpp_units_length_pixels;
                    case "ft":
                        return R.string.cpp_units_length_feet;
                    case "pc":
                        return R.string.cpp_units_length_parsecs;
                    case "pt":
                        return R.string.cpp_units_length_points;
                }
                break;
            case MASS:
                switch (id) {
                    case "kg":
                        return R.string.cpp_units_mass_kg;
                    case "lb":
                        return R.string.cpp_units_mass_lb;
                    case "oz":
                        return R.string.cpp_units_mass_oz;
                    case "t":
                        return R.string.cpp_units_mass_t;
                    case "ton_uk":
                        return R.string.cpp_units_mass_tons_uk;
                    case "ton_us":
                        return R.string.cpp_units_mass_tons_us;
                }
                break;
            case TEMPERATURE:
                // temperature unit ids are international
                return 0;
        }
        Log.w("Converter", "Unit translation is missing for unit=" + id + " in dimension=" + dimension);
        return 0;
    }

    public static double parse(@NonNull String value) {
        return parse(value, 10);
    }

    public static double parse(@NonNull String value, int base) {
        final String groupingSeparator = String.valueOf(JsclMathEngine.getInstance().getGroupingSeparator());
        if (!TextUtils.isEmpty(groupingSeparator)) {
            value = value.replace(groupingSeparator, "");
        }
        final Real real = new Real(value, base);
        final long bits = real.toDoubleBits();
        return Double.longBitsToDouble(bits);
    }
}
