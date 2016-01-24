package jscl.math.function;

import jscl.JsclMathEngine;
import jscl.MathEngine;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: serso
 * Date: 1/9/12
 * Time: 6:49 PM
 */
public class LnTest {

    @Test
    public void testConjugate() throws Exception {
        final MathEngine me = JsclMathEngine.getInstance();

        assertEquals("ln(5-i)", me.simplify("conjugate(ln(5+√(-1)))"));
        assertEquals("lg(5-i)", me.simplify("conjugate(lg(5+√(-1)))"));
    }

    @Test
    public void testAntiDerivative() throws Exception {
        final MathEngine me = JsclMathEngine.getInstance();

        assertEquals("-x+x*ln(x)", me.simplify("∫(ln(x), x)"));
        assertEquals("-(x-x*ln(x))/(ln(2)+ln(5))", me.simplify("∫(lg(x), x)"));
    }

    @Test
    public void testDerivative() throws Exception {
        final MathEngine me = JsclMathEngine.getInstance();

        assertEquals("1/x", me.simplify("∂(ln(x), x)"));
        assertEquals("1/(x*ln(2)+x*ln(5))", me.simplify("∂(lg(x), x)"));
    }
}
