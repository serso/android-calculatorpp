package org.solovyev.android;

import junit.framework.Assert;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.solovyev.android.calculator.units.CalculatorNumeralBase;
import org.solovyev.math.units.Unit;
import org.solovyev.math.units.UnitConverter;

import java.util.Date;
import java.util.Random;

/**
 * User: serso
 * Date: 4/21/12
 * Time: 8:24 PM
 */
public class AndroidNumeralBaseTest {

    @NotNull
    private final UnitConverter c = CalculatorNumeralBase.getConverter();

    @Test
    public void testIsSupported() throws Exception {
        Assert.assertTrue(c.isSupported(CalculatorNumeralBase.bin, CalculatorNumeralBase.dec));
    }

    @Test
    public void testConvertFromDec() throws Exception {

        Assert.assertEquals("101", c.convert(CalculatorNumeralBase.dec.createUnit("5"), CalculatorNumeralBase.bin).getValue());
        Assert.assertEquals("1", c.convert(CalculatorNumeralBase.dec.createUnit("1"), CalculatorNumeralBase.bin).getValue());
        Assert.assertEquals("0", c.convert(CalculatorNumeralBase.dec.createUnit("0"), CalculatorNumeralBase.bin).getValue());
        Assert.assertEquals("1111100111", c.convert(CalculatorNumeralBase.dec.createUnit("999"), CalculatorNumeralBase.bin).getValue());

        Assert.assertEquals("A23", c.convert(CalculatorNumeralBase.dec.createUnit("2595"), CalculatorNumeralBase.hex).getValue());
        Assert.assertEquals("AEE", c.convert(CalculatorNumeralBase.dec.createUnit("2798"), CalculatorNumeralBase.hex).getValue());
        Assert.assertEquals("15", c.convert(CalculatorNumeralBase.dec.createUnit("21"), CalculatorNumeralBase.hex).getValue());
        Assert.assertEquals("0", c.convert(CalculatorNumeralBase.dec.createUnit("0"), CalculatorNumeralBase.hex).getValue());
        Assert.assertEquals("3E7", c.convert(CalculatorNumeralBase.dec.createUnit("999"), CalculatorNumeralBase.hex).getValue());

        Assert.assertEquals("76", c.convert(CalculatorNumeralBase.dec.createUnit("62"), CalculatorNumeralBase.oct).getValue());
        Assert.assertEquals("12", c.convert(CalculatorNumeralBase.dec.createUnit("10"), CalculatorNumeralBase.oct).getValue());
        Assert.assertEquals("15", c.convert(CalculatorNumeralBase.dec.createUnit("13"), CalculatorNumeralBase.oct).getValue());
        Assert.assertEquals("0", c.convert(CalculatorNumeralBase.dec.createUnit("0"), CalculatorNumeralBase.oct).getValue());
        Assert.assertEquals("10445", c.convert(CalculatorNumeralBase.dec.createUnit("4389"), CalculatorNumeralBase.oct).getValue());
    }

    @Test
    public void testRandomConvert() throws Exception {
        final Random random = new Random(new Date().getTime());
        for (int i = 0; i < 100000; i++) {
            final String value = String.valueOf(random.nextInt());
            Assert.assertEquals(value, convertChain(value, CalculatorNumeralBase.dec, CalculatorNumeralBase.oct, CalculatorNumeralBase.oct, CalculatorNumeralBase.bin, CalculatorNumeralBase.dec));
            Assert.assertEquals(value, convertChain(value, CalculatorNumeralBase.dec, CalculatorNumeralBase.bin, CalculatorNumeralBase.hex, CalculatorNumeralBase.dec, CalculatorNumeralBase.dec));
            Assert.assertEquals(value, convertChain(value, CalculatorNumeralBase.dec, CalculatorNumeralBase.dec, CalculatorNumeralBase.hex, CalculatorNumeralBase.oct, CalculatorNumeralBase.dec));
            Assert.assertEquals(value, convertChain(value, CalculatorNumeralBase.dec, CalculatorNumeralBase.hex, CalculatorNumeralBase.bin, CalculatorNumeralBase.oct, CalculatorNumeralBase.dec));

        }
    }

    @NotNull
    private String convertChain(@NotNull String value, @NotNull CalculatorNumeralBase baseAndroid, @NotNull CalculatorNumeralBase... typeAndroids) {
        Unit<String> unit = baseAndroid.createUnit(value);

        for (CalculatorNumeralBase typeAndroid : typeAndroids) {
            unit = CalculatorNumeralBase.getConverter().convert(unit, typeAndroid);
        }

        return unit.getValue();
    }
}
