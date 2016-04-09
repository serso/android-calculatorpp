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

        assertEquals("0.9999060498015505+0.0137073546047075*i", me.evaluate("√(√(-1))"));
        assertEquals("0.9984971498638638+0.0548036651487895*i", me.evaluate("√(√(-1))^4"));

        try {
            me.setAngleUnits(AngleUnit.rad);
            assertEquals("0.7071067811865476+0.7071067811865475*i", me.evaluate("√(√(-1))"));
            assertEquals("-1+0.0000000000000003*i", me.evaluate("√(√(-1))^4"));
        } finally {
            me.setAngleUnits(defaultAngleUnits);
        }
    }
}
