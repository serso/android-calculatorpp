package jscl.math.function;

import jscl.JsclMathEngine;
import jscl.math.Expression;
import jscl.math.JsclInteger;
import org.junit.Test;

import static junit.framework.Assert.fail;

/**
 * User: serso
 * Date: 6/15/13
 * Time: 10:13 PM
 */
public class PowTest {

    @Test
    public void testPow() throws Exception {
        JsclMathEngine me = JsclMathEngine.getInstance();

        new Pow(Expression.valueOf("10"), new Inverse(JsclInteger.valueOf(10l)).expressionValue()).rootValue();
        try {
            new Pow(Expression.valueOf("10"), new Inverse(JsclInteger.valueOf(10000000000l)).expressionValue()).rootValue();
            fail();
        } catch (NotRootException e) {
            // ok
        }
    }
}
