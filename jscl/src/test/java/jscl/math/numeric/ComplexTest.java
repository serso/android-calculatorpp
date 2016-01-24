package jscl.math.numeric;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.math.Expression;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: serso
 * Date: 5/14/12
 * Time: 2:24 PM
 */
public class ComplexTest {

    @Test
    public void testSmallImag() throws Exception {
        assertEquals("1+100E-18*i", Complex.valueOf(1, 0.0000000000000001).toString());
        assertEquals("1-100E-18*i", Complex.valueOf(1, -0.0000000000000001).toString());
    }

    @Test
    public void testTrig() throws Exception {
        try {
            JsclMathEngine.getInstance().setAngleUnits(AngleUnit.rad);
            assertEquals("1.543080634815244", Expression.valueOf("cos(i)").numeric().toString());
            assertEquals("1.1752011936438014*i", Expression.valueOf("sin(i)").numeric().toString());
            assertEquals("11013.232874703395*i", Expression.valueOf("sin(10*i)").numeric().toString());
            assertEquals("11013.232920103324", Expression.valueOf("cos(10*i)").numeric().toString());
            assertEquals("0.46211715726000974*i", Expression.valueOf("tan(i/2)").numeric().toString());
            assertEquals("-2.163953413738653*i", Expression.valueOf("cot(i/2)").numeric().toString());
        } finally {
            JsclMathEngine.getInstance().setAngleUnits(JsclMathEngine.DEFAULT_ANGLE_UNITS);
        }
    }
}
