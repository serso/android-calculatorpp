package jscl.math.operator;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.NumericWrapper;
import jscl.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;

public class ModuloTest {

    @Test
    public void testNumeric() throws Exception {
        assertMod(2, 2);
        assertMod(1, 2);
        assertMod(3.5, 2);
        assertMod(3, 2);
        assertMod(1.5, 2);
        assertMod(1.5, "1.5", "2");
        assertMod(1.5, "3.5", "2");
    }

    private void assertMod(double expected, @Nonnull String numerator, @Nonnull String denominator) throws ParseException {
        final Modulo mod = makeModulo(numerator, denominator);
        final Generic numeric = mod.numeric();
        assertEquals(expected, numeric.doubleValue(), Math.pow(10, -8));
    }

    private void assertMod(int numerator, int denominator) {
        final Modulo mod = makeModulo(numerator, denominator);
        final Generic numeric = mod.numeric();
        Assert.assertTrue(numeric.isInteger());
        assertEquals(numerator % denominator, numeric.integerValue().intValue());
    }

    private void assertMod(double numerator, double denominator) {
        final Modulo mod = makeModulo(numerator, denominator);
        final Generic numeric = mod.numeric();
        assertEquals(numerator % denominator, numeric.doubleValue(), Math.pow(10, -8));
    }

    private Modulo makeModulo(int n, int d) {
        return new Modulo(NumericWrapper.valueOf(n), NumericWrapper.valueOf(d));
    }

    private Modulo makeModulo(String n, String d) throws ParseException {
        return new Modulo(Expression.valueOf(n), Expression.valueOf(d));
    }

    private Modulo makeModulo(double n, double d) {
        return new Modulo(NumericWrapper.valueOf(n), NumericWrapper.valueOf(d));
    }
}