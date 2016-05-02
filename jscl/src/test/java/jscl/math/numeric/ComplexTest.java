package jscl.math.numeric;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.math.Expression;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ComplexTest {

    @Test
    public void testSmallImag() throws Exception {
        assertEquals("1+0.000000000000001*i", Complex.valueOf(1, 0.000000000000001).toString());
        assertEquals("1-0.000000000000001*i", Complex.valueOf(1, -0.000000000000001).toString());
    }

    @Test
    public void testTrig() throws Exception {
        try {
            JsclMathEngine.getInstance().setAngleUnits(AngleUnit.rad);
            assertEquals("1.543080634815244", Expression.valueOf("cos(i)").numeric().toString());
            assertEquals("1.175201193643801*i", Expression.valueOf("sin(i)").numeric().toString());
            assertEquals("11013.2328747034*i", Expression.valueOf("sin(10*i)").numeric().toString());
            assertEquals("11013.23292010332", Expression.valueOf("cos(10*i)").numeric().toString());
            assertEquals("0.46211715726001*i", Expression.valueOf("tan(i/2)").numeric().toString());
            assertEquals("-2.163953413738653*i", Expression.valueOf("cot(i/2)").numeric().toString());
        } finally {
            JsclMathEngine.getInstance().setAngleUnits(JsclMathEngine.DEFAULT_ANGLE_UNITS);
        }
    }
}
