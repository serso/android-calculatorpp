/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android;

import android.os.Build;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.solovyev.android.calculator.BuildConfig;
import org.solovyev.android.calculator.CalculatorTestUtils;
import org.solovyev.android.calculator.units.CalculatorNumeralBase;
import org.solovyev.common.units.Unit;
import org.solovyev.common.units.UnitConverter;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * User: serso
 * Date: 4/21/12
 * Time: 8:24 PM
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class AndroidNumeralBaseTest {

    @Nonnull
    private final UnitConverter<String> c = CalculatorNumeralBase.getConverter();

    @BeforeClass
    public static void staticSetUp() throws Exception {
        CalculatorTestUtils.staticSetUp();
    }

    @Test
    public void testIsSupported() throws Exception {
        assertTrue(c.isSupported(CalculatorNumeralBase.bin, CalculatorNumeralBase.dec));
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

    @Nonnull
    private String convertChain(@Nonnull String value, @Nonnull CalculatorNumeralBase baseAndroid, @Nonnull CalculatorNumeralBase... typeAndroids) {
        Unit<String> unit = baseAndroid.createUnit(value);

        for (CalculatorNumeralBase typeAndroid : typeAndroids) {
            unit = CalculatorNumeralBase.getConverter().convert(unit, typeAndroid);
        }

        return unit.getValue();
    }
}
