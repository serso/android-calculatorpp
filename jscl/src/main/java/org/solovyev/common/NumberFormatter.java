package org.solovyev.common;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import midpcalc.Real;

import static java.lang.Math.pow;
import static midpcalc.Real.NumberFormat.FSE_ENG;
import static midpcalc.Real.NumberFormat.FSE_FIX;
import static midpcalc.Real.NumberFormat.FSE_NONE;
import static midpcalc.Real.NumberFormat.FSE_SCI;

public class NumberFormatter {

    public static final int NO_GROUPING = 0;
    public static final int NO_ROUNDING = -1;
    public static final int DEFAULT_MAGNITUDE = 5;
    public static final int MAX_PRECISION = 16;

    private final Real.NumberFormat numberFormat = new Real.NumberFormat();
    private final Real real = new Real();
    private int format = FSE_NONE;
    private int simpleFormatMagnitude = DEFAULT_MAGNITUDE;
    private int precision = MAX_PRECISION;
    private char groupingSeparator;

    public void useScientificFormat(int simpleFormatMagnitude) {
        this.format = FSE_SCI;
        this.simpleFormatMagnitude = simpleFormatMagnitude;
    }

    public void useEngineeringFormat(int simpleFormatMagnitude) {
        this.format = FSE_ENG;
        this.simpleFormatMagnitude = simpleFormatMagnitude;
    }

    public void useSimpleFormat() {
        this.format = FSE_NONE;
        this.simpleFormatMagnitude = DEFAULT_MAGNITUDE;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public void setGroupingSeparator(char groupingSeparator) {
        this.groupingSeparator = groupingSeparator;
    }

    @Nonnull
    public CharSequence format(double value) {
        return format(value, 10);
    }

    @Nonnull
    public CharSequence format(double value, int radix) {
        if (radix != 2 && radix != 8 && radix != 10 && radix != 16) {
            throw new IllegalArgumentException("Unsupported radix: " + radix);
        }
        double absValue = Math.abs(value);
        final boolean simpleFormat = useSimpleFormat(radix, absValue);

        final int effectivePrecision = precision == NO_ROUNDING ? MAX_PRECISION : precision;
        if (simpleFormat) {
            final int newScale = (int) (effectivePrecision * Math.max(1, radix / 10f));
            value = BigDecimal.valueOf(value).setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
            absValue = Math.abs(value);
        }
        if (simpleFormat) {
            numberFormat.fse = FSE_FIX;
        } else if (format == FSE_NONE) {
            // originally, a simple format was requested but we have to use something more appropriate, f.e. scientific
            // format
            numberFormat.fse = FSE_SCI;
        } else {
            numberFormat.fse = format;
        }
        numberFormat.thousand = groupingSeparator;
        numberFormat.precision = effectivePrecision;
        numberFormat.base = radix;
        numberFormat.maxwidth = simpleFormat ? 100 : 30;

        if (radix == 2 && value < 0) {
            return "-" + prepare(absValue);
        }
        return prepare(value);
    }

    private boolean useSimpleFormat(int radix, double absValue) {
        if (radix != 10) {
            return true;
        }
        if (format == FSE_NONE) {
            // simple format should be used only if rounding is on or if number is big enough
            final boolean round = precision != NO_ROUNDING;
            return round || absValue >= pow(10, -MAX_PRECISION);
        }
        if (pow(10, -simpleFormatMagnitude) <= absValue && absValue < pow(10, simpleFormatMagnitude)) {
            return true;
        }
        return false;
    }

    @Nonnull
    private CharSequence prepare(double value) {
        return stripZeros(realFormat(value)).replace('e', 'E');
    }

    @Nonnull
    private String realFormat(double value) {
        real.assign(Double.toString(value));
        return real.toString(numberFormat);
    }

    @Nonnull
    private String stripZeros(@Nonnull String s) {
        int dot = -1;
        int firstNonZero = -1;
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (c != '0' && c != groupingSeparator && firstNonZero == -1) {
                firstNonZero = i;
            }
            if (c == '.') {
                dot = i;
                break;
            }
        }
        if (firstNonZero == -1) {
            // all zeros
            return "";
        }
        if (dot < 0) {
            // no dot - no trailing zeros
            return s.substring(firstNonZero);
        }
        if (firstNonZero == dot) {
            // one zero before dot must be kept
            firstNonZero--;
        }
        final int e = s.lastIndexOf('e');
        final int i = findLastNonZero(s, e);
        final int end = i == dot ? i : i + 1;
        return s.substring(firstNonZero, end) + getExponent(s, e);
    }

    @Nonnull
    private String getExponent(@Nonnull String s, int e) {
        String exponent = "";
        if (e > 0) {
            exponent = s.substring(e);
            if (exponent.length() == 2 && exponent.charAt(1) == '0') {
                exponent = "";
            }
        }
        return exponent;
    }

    private int findLastNonZero(@Nonnull String s, int e) {
        int i = e > 0 ? e - 1 : s.length() - 1;
        for (; i >= 0; i--) {
            if (s.charAt(i) != '0') {
                break;
            }
        }
        return i;
    }
}
