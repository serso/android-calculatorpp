package jscl.math.function.trigonometric;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 1/7/12
 * Time: 3:51 PM
 */
public class SinTest {

    @Test
    public void testIntegrate() throws Exception {
        final JsclMathEngine me = JsclMathEngine.getInstance();

        // todo serso: uncomment after variable modification issue fixed
        /*Assert.assertEquals("-cos(x)", me.simplify("∫(sin(x), x)"));
          Assert.assertEquals("-cos(x*π)/π", me.simplify("∫(sin(π*x), x)"));

          Assert.assertEquals("1.0", me.evaluate("cos(0)"));
          Assert.assertEquals("0.8660254037844387", me.evaluate("cos(30)"));
          Assert.assertEquals("0.1339745962155613", me.evaluate("∫ab(sin(x), x, 0, 30)"));*/

        try {
            me.setAngleUnits(AngleUnit.rad);
            Assert.assertEquals("0.54030230586814", me.evaluate("cos(1)"));
            Assert.assertEquals("0.362357754476674", me.evaluate("cos(1.2)"));
            Assert.assertEquals("0.177944551391466", me.evaluate("∫ab(sin(x), x, 1, 1.2)"));
        } finally {
            me.setAngleUnits(AngleUnit.deg);
        }


        //Assert.assertEquals("7.676178925", me.evaluate("∫ab(sin(x), x, 0, 30°)"));

        try {
            me.setAngleUnits(AngleUnit.rad);
            Assert.assertEquals("0.133974596215561", me.evaluate("∫ab(sin(x), x, 0, 30°)"));
        } finally {
            me.setAngleUnits(AngleUnit.deg);
        }
    }
}
