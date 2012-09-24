package org.solovyev.android.calculator.units;

import jscl.NumeralBase;
import org.jetbrains.annotations.NotNull;
import org.solovyev.math.units.Unit;
import org.solovyev.math.units.UnitConverter;
import org.solovyev.math.units.UnitImpl;
import org.solovyev.math.units.UnitType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Solovyev_S
 * Date: 24.09.12
 * Time: 16:05
 */
public enum CalculatorNumeralBase implements UnitType<String> {


    bin(NumeralBase.bin),

    oct(NumeralBase.oct),

    dec(NumeralBase.dec),

    hex(NumeralBase.hex);

    @NotNull
    private final NumeralBase numeralBase;

    private CalculatorNumeralBase(@NotNull NumeralBase numeralBase) {
        this.numeralBase = numeralBase;
    }

    @NotNull
    public NumeralBase getNumeralBase() {
        return numeralBase;
    }

    @NotNull
    private static final CalculatorNumeralBase.Converter converter = new CalculatorNumeralBase.Converter();

    @NotNull
    public static CalculatorNumeralBase.Converter getConverter() {
        return converter;
    }

    @NotNull
    @Override
    public Class<String> getUnitValueClass() {
        return String.class;
    }

    @NotNull
    public Unit<String> createUnit(@NotNull String value) {
        return UnitImpl.newInstance(value, this);
    }

    public static class Converter implements UnitConverter<String> {

        private Converter() {
        }

        @Override
        public boolean isSupported(@NotNull UnitType<?> from, @NotNull UnitType<String> to) {
            return CalculatorNumeralBase.class.isAssignableFrom(from.getClass()) && CalculatorNumeralBase.class.isAssignableFrom(to.getClass());
        }

        @NotNull
        @Override
        public Unit<String> convert(@NotNull Unit<?> from, @NotNull UnitType<String> toType) {
            if (!isSupported(from.getUnitType(), toType)) {
                throw new IllegalArgumentException("Types are not supported!");
            }

            final CalculatorNumeralBase fromTypeAndroid = (CalculatorNumeralBase) from.getUnitType();
            final NumeralBase fromNumeralBase = fromTypeAndroid.numeralBase;
            final NumeralBase toNumeralBase = ((CalculatorNumeralBase) toType).numeralBase;
            final String fromValue = (String) from.getValue();

            final BigInteger decBigInteger = fromNumeralBase.toBigInteger(fromValue);
            return UnitImpl.newInstance(toNumeralBase.toString(decBigInteger), toType);
        }
    }

    @NotNull
    public static CalculatorNumeralBase valueOf(@NotNull NumeralBase nb) {
        for (CalculatorNumeralBase calculatorNumeralBase : values()) {
            if (calculatorNumeralBase.numeralBase == nb) {
                return calculatorNumeralBase;
            }
        }

        throw new IllegalArgumentException(nb + " is not supported numeral base!");
    }
}
