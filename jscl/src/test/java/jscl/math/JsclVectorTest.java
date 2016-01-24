package jscl.math;

import jscl.JsclMathEngine;
import jscl.MathEngine;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 12/26/11
 * Time: 9:52 AM
 */
public class JsclVectorTest {

    @Test
    public void testVector() throws Exception {
        MathEngine me = JsclMathEngine.getInstance();
        Assert.assertEquals("[1, 0, 0, 1]", me.evaluate("[1, 0, 0, 1]"));
    }
}
