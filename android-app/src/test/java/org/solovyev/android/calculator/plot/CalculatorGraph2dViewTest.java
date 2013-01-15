package org.solovyev.android.calculator.plot;

import junit.framework.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 1/15/13
 * Time: 9:58 PM
 */
public class CalculatorGraph2dViewTest {

    @Test
    public void testFormatTick() throws Exception {
        Assert.assertEquals("23324", CalculatorGraph2dView.formatTick(23324.0f, 0));
        Assert.assertEquals("23324.1", CalculatorGraph2dView.formatTick(23324.1f, 1));
    }

    @Test
    public void testCountTickDigits() throws Exception {
        Assert.assertEquals(0, CalculatorGraph2dView.countTickDigits(1));
        Assert.assertEquals(0, CalculatorGraph2dView.countTickDigits(10));
        Assert.assertEquals(0, CalculatorGraph2dView.countTickDigits(100));
        Assert.assertEquals(1, CalculatorGraph2dView.countTickDigits(0.9f));
        Assert.assertEquals(1, CalculatorGraph2dView.countTickDigits(0.2f));
        Assert.assertEquals(1, CalculatorGraph2dView.countTickDigits(0.1f));
        Assert.assertEquals(2, CalculatorGraph2dView.countTickDigits(0.099f));
        Assert.assertEquals(3, CalculatorGraph2dView.countTickDigits(0.009f));
    }
}
