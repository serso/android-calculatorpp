package org.solovyev.android;

import jscl.NumeralBase;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

/**
 * User: serso
 * Date: 4/21/12
 * Time: 8:00 PM
 */
public enum NumeralBaseUnitType implements UnitType<String> {

    bin(NumeralBase.bin),
    oct(NumeralBase.oct),
    dec(NumeralBase.dec),
    hex(NumeralBase.hex);

    @NotNull
    private final NumeralBase numeralBase;

    private NumeralBaseUnitType(@NotNull NumeralBase numeralBase) {
        this.numeralBase = numeralBase;
    }

    @NotNull
    public Unit<String> createUnit(@NotNull String value) {
        return UnitImpl.newInstance(value, this);
    }

    @NotNull
    @Override
    public Class<String> getUnitValueClass() {
        return String.class;
    }

    @NotNull
    private static final Converter converter = new Converter();

    @NotNull
    public static Converter getConverter() {
        return converter;
    }

    public static class Converter implements UnitConverter<String> {

        private Converter() {
        }

        @Override
        public boolean isSupported(@NotNull UnitType<?> from, @NotNull UnitType<String> to) {
            return NumeralBaseUnitType.class.isAssignableFrom(from.getClass()) && NumeralBaseUnitType.class.isAssignableFrom(to.getClass());
        }

        @NotNull
        @Override
        public Unit<String> convert(@NotNull Unit<?> from, @NotNull UnitType<String> toType) {
            if (!isSupported(from.getUnitType(), toType)) {
                throw new IllegalArgumentException("Types are not supported!");
            }

            final NumeralBaseUnitType fromType = (NumeralBaseUnitType) from.getUnitType();
            final NumeralBase fromNumeralBase = fromType.numeralBase;
            final NumeralBase toNumeralBase = ((NumeralBaseUnitType) toType).numeralBase;
            final String fromValue = (String) from.getValue();

            final BigInteger decBigInteger = fromNumeralBase.toBigInteger(fromValue);
            return UnitImpl.newInstance(toNumeralBase.toString(decBigInteger), (NumeralBaseUnitType) toType);
        }
    }

    @NotNull
    public static NumeralBaseUnitType valueOf(@NotNull NumeralBase nb ) {
        for (NumeralBaseUnitType numeralBaseUnitType : values()) {
            if ( numeralBaseUnitType.numeralBase == nb ) {
                return numeralBaseUnitType;
            }
        }

        throw new IllegalArgumentException(nb + " is not supported numeral base!");
    }
}
