package jscl.math.operator;

import jscl.JsclMathEngine;
import jscl.math.function.Constant;
import jscl.math.function.ExtendedConstant;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SumTest {

    @Test
    public void testExp() throws Exception {
        final JsclMathEngine me = JsclMathEngine.getInstance();
        final ExtendedConstant.Builder x = new ExtendedConstant.Builder(new Constant("x"), 2d);
        me.getConstantsRegistry().addOrUpdate(x.create());
        final ExtendedConstant.Builder i = new ExtendedConstant.Builder(new Constant("i"), (String) null);
        me.getConstantsRegistry().addOrUpdate(i.create());
        assertEquals("51.73529646243829", me.evaluate("Σ((1+x/i)^i, i, 1, 10)"));
        assertEquals("686.0048440525586", me.evaluate("Σ((1+x/i)^i, i, 1, 100)"));
    }
}
