package jscl;

import jscl.math.JsclInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public enum NumeralBase {

    dec(10, 3) {

        private final List<Character> characters = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

        @Nonnull
        @Override
        public Double toDouble(@Nonnull String doubleString) {
            return Double.valueOf(doubleString);
        }

        @Nonnull
        public String toString(@Nonnull Double value) {
            return value.toString();
        }

        @Nonnull
        @Override
        public String getJsclPrefix() {
            return "0d:";
        }

        @Nonnull
        @Override
        public List<Character> getAcceptableCharacters() {
            return characters;
        }
    },

    hex(16, 2) {

        private final List<Character> characters = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F');

        @Nonnull
        @Override
        public String getJsclPrefix() {
            return "0x:";
        }

        @Nonnull
        @Override
        public List<Character> getAcceptableCharacters() {
            return characters;
        }
    },

    oct(8, 4) {

        private final List<Character> characters = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7');

        @Nonnull
        @Override
        public String getJsclPrefix() {
            return "0o:";
        }

        @Nonnull
        @Override
        public List<Character> getAcceptableCharacters() {
            return characters;
        }
    },

    bin(2, 4) {

        private final List<Character> characters = Arrays.asList('0', '1');

        @Nonnull
        @Override
        public String getJsclPrefix() {
            return "0b:";
        }

        @Nonnull
        @Override
        public List<Character> getAcceptableCharacters() {
            return characters;
        }
    };

    protected final int radix;
    protected final int groupingSize;

    NumeralBase(int radix, int groupingSize) {
        this.radix = radix;
        this.groupingSize = groupingSize;
    }

    @Nullable
    public static NumeralBase getByPrefix(@Nonnull String prefix) {
        for (NumeralBase nb : NumeralBase.values()) {
            if (prefix.equals(nb.getJsclPrefix())) {
                return nb;
            }
        }

        return null;
    }

    @Nonnull
    protected static String toString(@Nonnull Double value, int radix, int fractionDigits) {
        final BigDecimal mult = BigDecimal.valueOf(radix).pow(fractionDigits);
        final BigDecimal bd = BigDecimal.valueOf(value).multiply(mult);

        final BigInteger bi = bd.toBigInteger();
        final StringBuilder result = new StringBuilder(bi.toString(radix));

        while (result.length() < fractionDigits + 1) {  // +1 for leading zero
            result.insert(0, "0");
        }
        result.insert(result.length() - fractionDigits, ".");

        return result.toString().toUpperCase();
    }

    @Nonnull
    public Double toDouble(@Nonnull String doubleString) throws NumberFormatException {
        return Double.longBitsToDouble(Long.valueOf(doubleString, radix));
    }

    @Nonnull
    public Integer toInteger(@Nonnull String integerString) throws NumberFormatException {
        return Integer.valueOf(integerString, radix);
    }

    @Nonnull
    public JsclInteger toJsclInteger(@Nonnull String integerString) throws NumberFormatException {
        return new JsclInteger(toBigInteger(integerString));
    }

    @Nonnull
    public BigInteger toBigInteger(@Nonnull String value) throws NumberFormatException {
        return new BigInteger(value, radix);
    }

    public String toString(@Nonnull BigInteger value) {
        return value.toString(radix).toUpperCase();
    }

    public String toString(@Nonnull Integer value) {
        return Integer.toString(value, radix).toUpperCase();
    }

    @Nonnull
    public abstract String getJsclPrefix();

    @Nonnull
    public abstract List<Character> getAcceptableCharacters();

    public int getGroupingSize() {
        return groupingSize;
    }

    @Nonnull
    public String toString(@Nonnull Double value, int fractionDigits) {
        return toString(value, radix, fractionDigits);
    }
}
