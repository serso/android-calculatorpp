package jscl.math.function.trigonometric;

import jscl.JsclMathEngine;
import jscl.math.function.Constant;
import jscl.math.function.ExtendedConstant;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 6/17/13
 * Time: 10:36 PM
 */
public class CosTest {

    @Test
    public void testIntegral() throws Exception {
        final JsclMathEngine me = JsclMathEngine.getInstance();
        me.getConstantsRegistry().add(new ExtendedConstant.Builder(new Constant("t"), 10d));
        Assert.assertEquals("-sin(t)", me.simplify("∂(cos(t),t,t,1)"));
        Assert.assertEquals("∂(cos(t), t, t, 1°)", me.simplify("∂(cos(t),t,t,1°)"));
        Assert.assertEquals("-0.17364817766693033", me.evaluate("∂(cos(t),t,t,1)"));
        Assert.assertEquals("∂(cos(t), t, t, 1°)", me.evaluate("∂(cos(t),t,t,1°)"));
        Assert.assertEquals("-0.17364817766693033", me.evaluate("∂(cos(t),t,t,2-1)"));
        Assert.assertEquals("-0.17364817766693033", me.evaluate("∂(cos(t),t,t,2^5-31)"));
    }
}
