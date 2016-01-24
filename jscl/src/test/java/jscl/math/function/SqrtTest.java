package jscl.math.function;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 5/14/12
 * Time: 1:15 PM
 */
public class SqrtTest {

    @Test
    public void testNumeric() throws Exception {
        final JsclMathEngine me = JsclMathEngine.getInstance();
        final AngleUnit defaultAngleUnits = me.getAngleUnits();

        Assert.assertEquals("0.9999060498015505+0.013707354604707477*i", me.evaluate("√(√(-1))"));
        Assert.assertEquals("0.9984971498638638+0.05480366514878954*i", me.evaluate("√(√(-1))^4"));

        try {
            me.setAngleUnits(AngleUnit.rad);
            Assert.assertEquals("0.7071067811865476+0.7071067811865475*i", me.evaluate("√(√(-1))"));
            Assert.assertEquals("-1+277.55575615628914E-18*i", me.evaluate("√(√(-1))^4"));
        } finally {
            me.setAngleUnits(defaultAngleUnits);
        }
    }
}
