package jscl.math.operator;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.MathEngine;
import jscl.math.Expression;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 1/14/12
 * Time: 1:06 PM
 */
public class IndefiniteIntegralTest {

    @Test
    public void testIntegral() throws Exception {
        final MathEngine me = JsclMathEngine.getInstance();

        try {
            Assert.assertEquals("∫(sin(t!), t)", me.evaluate("∫(sin(t!), t)"));
            Assert.fail();
        } catch (ArithmeticException e) {
            // ok
        }

        try {
            me.setAngleUnits(AngleUnit.rad);
            Assert.assertEquals("-cos(t)", Expression.valueOf("∫(sin(t), t)").expand().toString());
            Assert.assertEquals("∫(sin(t!), t)", Expression.valueOf("∫(sin(t!), t)").expand().toString());
            Assert.assertEquals("∫(sin(t!), t)", me.simplify("∫(sin(t!), t)"));
            Assert.assertEquals("∫(sin(t°), t)", Expression.valueOf("∫(sin(t°), t)").expand().toString());
            Assert.assertEquals("∫(sin(t°), t)", me.simplify("∫(sin(t°), t)"));
        } finally {
            me.setAngleUnits(AngleUnit.deg);
        }


    }
}
