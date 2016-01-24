package jscl.math.function;

import jscl.JsclMathEngine;
import junit.framework.Assert;
import org.junit.Test;
import org.solovyev.common.math.Maths;

/**
 * User: serso
 * Date: 11/12/11
 * Time: 4:17 PM
 */
public class DegTest {

    @Test
    public void testDeg() throws Exception {
        final JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        Assert.assertEquals("2", mathEngine.evaluate("deg(0.03490658503988659)"));
        Assert.assertEquals("-2", mathEngine.evaluate("deg(-0.03490658503988659)"));
        Assert.assertEquals("180", mathEngine.evaluate("deg(" + String.valueOf(Math.PI) + ")"));

        for (int i = 0; i < 1000; i++) {
            double value = Math.random() * 100000;
            assertEquals(value, Double.valueOf(mathEngine.evaluate("rad(deg(" + value + "))")));
            assertEquals(value, Double.valueOf(mathEngine.evaluate("deg(rad(" + value + "))")));
        }
    }

    private void assertEquals(double expected, Double actual) {
        Assert.assertTrue("Expected=" + expected + ", actual=" + actual, Maths.equals(expected, actual, 8));
    }
}
