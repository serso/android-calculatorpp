package org.solovyev.android;

import junit.framework.Assert;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.solovyev.android.calculator.AndroidNumeralBase;

import java.util.Date;
import java.util.Random;

/**
 * User: serso
 * Date: 4/21/12
 * Time: 8:24 PM
 */
public class AndroidNumeralBaseTest {

    @NotNull
    private final UnitConverter c = AndroidNumeralBase.getConverter();

    @Test
    public void testIsSupported() throws Exception {
        Assert.assertTrue(c.isSupported(AndroidNumeralBase.bin, AndroidNumeralBase.dec));
    }

    @Test
    public void testConvertFromDec() throws Exception {

        Assert.assertEquals("101", c.convert(AndroidNumeralBase.dec.createUnit("5"), AndroidNumeralBase.bin).getValue());
        Assert.assertEquals("1", c.convert(AndroidNumeralBase.dec.createUnit("1"), AndroidNumeralBase.bin).getValue());
        Assert.assertEquals("0", c.convert(AndroidNumeralBase.dec.createUnit("0"), AndroidNumeralBase.bin).getValue());
        Assert.assertEquals("1111100111", c.convert(AndroidNumeralBase.dec.createUnit("999"), AndroidNumeralBase.bin).getValue());

        Assert.assertEquals("A23", c.convert(AndroidNumeralBase.dec.createUnit("2595"), AndroidNumeralBase.hex).getValue());
        Assert.assertEquals("AEE", c.convert(AndroidNumeralBase.dec.createUnit("2798"), AndroidNumeralBase.hex).getValue());
        Assert.assertEquals("15", c.convert(AndroidNumeralBase.dec.createUnit("21"), AndroidNumeralBase.hex).getValue());
        Assert.assertEquals("0", c.convert(AndroidNumeralBase.dec.createUnit("0"), AndroidNumeralBase.hex).getValue());
        Assert.assertEquals("3E7", c.convert(AndroidNumeralBase.dec.createUnit("999"), AndroidNumeralBase.hex).getValue());

        Assert.assertEquals("76", c.convert(AndroidNumeralBase.dec.createUnit("62"), AndroidNumeralBase.oct).getValue());
        Assert.assertEquals("12", c.convert(AndroidNumeralBase.dec.createUnit("10"), AndroidNumeralBase.oct).getValue());
        Assert.assertEquals("15", c.convert(AndroidNumeralBase.dec.createUnit("13"), AndroidNumeralBase.oct).getValue());
        Assert.assertEquals("0", c.convert(AndroidNumeralBase.dec.createUnit("0"), AndroidNumeralBase.oct).getValue());
        Assert.assertEquals("10445", c.convert(AndroidNumeralBase.dec.createUnit("4389"), AndroidNumeralBase.oct).getValue());
    }

    @Test
    public void testRandomConvert() throws Exception {
        final Random random = new Random(new Date().getTime());
        for (int i = 0; i < 100000; i++) {
            final String value = String.valueOf(random.nextInt());
            Assert.assertEquals(value, convertChain(value, AndroidNumeralBase.dec, AndroidNumeralBase.oct, AndroidNumeralBase.oct, AndroidNumeralBase.bin, AndroidNumeralBase.dec));
            Assert.assertEquals(value, convertChain(value, AndroidNumeralBase.dec, AndroidNumeralBase.bin, AndroidNumeralBase.hex, AndroidNumeralBase.dec, AndroidNumeralBase.dec));
            Assert.assertEquals(value, convertChain(value, AndroidNumeralBase.dec, AndroidNumeralBase.dec, AndroidNumeralBase.hex, AndroidNumeralBase.oct, AndroidNumeralBase.dec));
            Assert.assertEquals(value, convertChain(value, AndroidNumeralBase.dec, AndroidNumeralBase.hex, AndroidNumeralBase.bin, AndroidNumeralBase.oct, AndroidNumeralBase.dec));

        }
    }

    @NotNull
    private String convertChain(@NotNull String value, @NotNull AndroidNumeralBase baseAndroid, @NotNull AndroidNumeralBase... typeAndroids) {
        Unit<String> unit = baseAndroid.createUnit(value);

        for (AndroidNumeralBase typeAndroid : typeAndroids) {
            unit = AndroidNumeralBase.getConverter().convert(unit, typeAndroid);
        }

        return unit.getValue();
    }
}
