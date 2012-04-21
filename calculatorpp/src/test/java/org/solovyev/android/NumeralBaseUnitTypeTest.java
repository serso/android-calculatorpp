package org.solovyev.android;

import junit.framework.Assert;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Date;
import java.util.Random;

/**
 * User: serso
 * Date: 4/21/12
 * Time: 8:24 PM
 */
public class NumeralBaseUnitTypeTest {

    @NotNull
    private final UnitConverter c = NumeralBaseUnitType.getConverter();

    @Test
    public void testIsSupported() throws Exception {
        Assert.assertTrue(c.isSupported(NumeralBaseUnitType.bin, NumeralBaseUnitType.dec));
    }

    @Test
    public void testConvertFromDec() throws Exception {

        Assert.assertEquals("101", c.convert(NumeralBaseUnitType.dec.createUnit("5"), NumeralBaseUnitType.bin).getValue());
        Assert.assertEquals("1", c.convert(NumeralBaseUnitType.dec.createUnit("1"), NumeralBaseUnitType.bin).getValue());
        Assert.assertEquals("0", c.convert(NumeralBaseUnitType.dec.createUnit("0"), NumeralBaseUnitType.bin).getValue());
        Assert.assertEquals("1111100111", c.convert(NumeralBaseUnitType.dec.createUnit("999"), NumeralBaseUnitType.bin).getValue());

        Assert.assertEquals("A23", c.convert(NumeralBaseUnitType.dec.createUnit("2595"), NumeralBaseUnitType.hex).getValue());
        Assert.assertEquals("AEE", c.convert(NumeralBaseUnitType.dec.createUnit("2798"), NumeralBaseUnitType.hex).getValue());
        Assert.assertEquals("15", c.convert(NumeralBaseUnitType.dec.createUnit("21"), NumeralBaseUnitType.hex).getValue());
        Assert.assertEquals("0", c.convert(NumeralBaseUnitType.dec.createUnit("0"), NumeralBaseUnitType.hex).getValue());
        Assert.assertEquals("3E7", c.convert(NumeralBaseUnitType.dec.createUnit("999"), NumeralBaseUnitType.hex).getValue());

        Assert.assertEquals("76", c.convert(NumeralBaseUnitType.dec.createUnit("62"), NumeralBaseUnitType.oct).getValue());
        Assert.assertEquals("12", c.convert(NumeralBaseUnitType.dec.createUnit("10"), NumeralBaseUnitType.oct).getValue());
        Assert.assertEquals("15", c.convert(NumeralBaseUnitType.dec.createUnit("13"), NumeralBaseUnitType.oct).getValue());
        Assert.assertEquals("0", c.convert(NumeralBaseUnitType.dec.createUnit("0"), NumeralBaseUnitType.oct).getValue());
        Assert.assertEquals("10445", c.convert(NumeralBaseUnitType.dec.createUnit("4389"), NumeralBaseUnitType.oct).getValue());
    }

    @Test
    public void testRandomConvert() throws Exception {
        final Random random = new Random(new Date().getTime());
        for (int i = 0; i < 100000; i++) {
            final String value = String.valueOf(random.nextInt());
            Assert.assertEquals(value, convertChain(value, NumeralBaseUnitType.dec, NumeralBaseUnitType.oct, NumeralBaseUnitType.oct, NumeralBaseUnitType.bin, NumeralBaseUnitType.dec));
            Assert.assertEquals(value, convertChain(value, NumeralBaseUnitType.dec, NumeralBaseUnitType.bin, NumeralBaseUnitType.hex, NumeralBaseUnitType.dec, NumeralBaseUnitType.dec));
            Assert.assertEquals(value, convertChain(value, NumeralBaseUnitType.dec, NumeralBaseUnitType.dec, NumeralBaseUnitType.hex, NumeralBaseUnitType.oct, NumeralBaseUnitType.dec));
            Assert.assertEquals(value, convertChain(value, NumeralBaseUnitType.dec, NumeralBaseUnitType.hex, NumeralBaseUnitType.bin, NumeralBaseUnitType.oct, NumeralBaseUnitType.dec));

        }
    }

    @NotNull
    private String convertChain(@NotNull String value, @NotNull NumeralBaseUnitType baseUnitType, @NotNull NumeralBaseUnitType... types) {
        Unit<String> unit = baseUnitType.createUnit(value);

        for (NumeralBaseUnitType type : types) {
            unit = NumeralBaseUnitType.getConverter().convert(unit, type);
        }

        return unit.getValue();
    }
}
