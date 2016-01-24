package jscl.math;

import jscl.JsclMathEngine;
import jscl.util.ExpressionGenerator;
import org.junit.Test;

/**
 * User: serso
 * Date: 12/14/11
 * Time: 10:40 PM
 */
public class RandomExpressionTest {

    public static final int MAX = 1000;

    @Test
    public void testRandomExpressions() throws Exception {
        final ExpressionGenerator eg = new ExpressionGenerator(20);
        int i = 0;
        while (i < MAX) {
            final String expression = eg.generate();
            final String result = JsclMathEngine.getInstance().evaluate(expression);

            //System.out.println(result + "-(" + expression + ")");

            i++;
        }
    }
}
