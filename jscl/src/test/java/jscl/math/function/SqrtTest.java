package jscl.math.function;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SqrtTest {

    @Test
    public void testNumeric() throws Exception {
        final JsclMathEngine me = JsclMathEngine.getInstance();
        final AngleUnit defaultAngleUnits = me.getAngleUnits();

        assertEquals("0.999906049801551+0.013707354604707*i", me.evaluate("√(√(-1))"));
        assertEquals("0.998497149863864+0.05480366514879*i", me.evaluate("√(√(-1))^4"));

        try {
            me.setAngleUnits(AngleUnit.rad);
            assertEquals("0.707106781186548+0.707106781186548*i", me.evaluate("√(√(-1))"));
            assertEquals("-1+0*i", me.evaluate("√(√(-1))^4"));
        } finally {
            me.setAngleUnits(defaultAngleUnits);
        }
    }
}
