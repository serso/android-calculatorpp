package jscl.math.function;

import jscl.JsclMathEngine;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 2/10/12
 * Time: 9:35 PM
 */
public class SgnTest {

    @Test
    public void testSgn() throws Exception {
        final JsclMathEngine me = JsclMathEngine.getInstance();

        Assert.assertEquals("1", me.evaluate("sgn(10)"));
        Assert.assertEquals("1", me.evaluate("sgn(0.5)"));
        Assert.assertEquals("0", me.evaluate("sgn(0)"));
        Assert.assertEquals("0", me.evaluate("sgn(-0)"));
        Assert.assertEquals("-1", me.evaluate("sgn(-1)"));
        Assert.assertEquals("-1", me.evaluate("sgn(-10)"));
    }
}
