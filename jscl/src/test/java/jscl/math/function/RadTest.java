package jscl.math.function;

import jscl.JsclMathEngine;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 11/12/11
 * Time: 4:00 PM
 */
public class RadTest {
    @Test
    public void testRad() throws Exception {
        final JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        Assert.assertEquals("0.03490658503988659", mathEngine.evaluate("rad(2)"));
        Assert.assertEquals("0.03490658503988659", mathEngine.evaluate("rad(1+1)"));
        Assert.assertEquals("-0.03490658503988659", mathEngine.evaluate("rad(-2)"));
        Assert.assertEquals("-0.03490658503988659", mathEngine.evaluate("rad(-1-1)"));
        Assert.assertEquals("π", mathEngine.evaluate("rad(180)"));
        Assert.assertEquals(String.valueOf(-Math.PI), mathEngine.evaluate("rad(-180)"));

        // todo serso: think about zeroes
        Assert.assertEquals("rad(-180, 0, 0)", mathEngine.simplify("rad(-180)"));
        Assert.assertEquals("rad(2, 0, 0)", mathEngine.simplify("rad(1+1)"));

        Assert.assertEquals("rad(-180, 0, 0)", mathEngine.elementary("rad(-180)"));
        Assert.assertEquals("rad(2, 0, 0)", mathEngine.elementary("rad(1+1)"));

        Assert.assertEquals(mathEngine.evaluate("rad(43.1025)"), mathEngine.evaluate("rad(43,6,9)"));
        Assert.assertEquals(mathEngine.evaluate("rad(102.765)"), mathEngine.evaluate("rad(102, 45,  54)"));
    }
}
