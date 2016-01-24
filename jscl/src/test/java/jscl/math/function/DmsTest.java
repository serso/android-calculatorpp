package jscl.math.function;

import jscl.JsclMathEngine;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 11/14/11
 * Time: 1:46 PM
 */
public class DmsTest {
    @Test
    public void testFunction() throws Exception {
        final JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        Assert.assertEquals("43.1025", mathEngine.evaluate("dms(43,6,9)"));
        Assert.assertEquals("102.765", mathEngine.evaluate("dms(102, 45,  54)"));
    }
}
