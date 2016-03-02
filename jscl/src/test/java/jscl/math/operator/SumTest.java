package jscl.math.operator;

import jscl.JsclMathEngine;
import jscl.math.function.Constant;
import jscl.math.function.ExtendedConstant;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 1/30/12
 * Time: 4:17 PM
 */
public class SumTest {

    @Test
    public void testExp() throws Exception {
        final JsclMathEngine me = JsclMathEngine.getInstance();
        final ExtendedConstant.Builder x = new ExtendedConstant.Builder(new Constant("x"), 2d);
        me.getConstantsRegistry().addOrUpdate(x.create());
        final ExtendedConstant.Builder i = new ExtendedConstant.Builder(new Constant("i"), (String) null);
        me.getConstantsRegistry().addOrUpdate(i.create());
        Assert.assertEquals("51.735296462438285", me.evaluate("Σ((1+x/i)^i, i, 1, 10)"));
        Assert.assertEquals("686.0048440525586", me.evaluate("Σ((1+x/i)^i, i, 1, 100)"));
    }
}
