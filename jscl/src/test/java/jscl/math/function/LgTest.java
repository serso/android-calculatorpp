package jscl.math.function;

import jscl.math.Expression;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: serso
 * Date: 6/15/13
 * Time: 12:52 AM
 */
public class LgTest {

    @Test
    public void testSimplify() throws Exception {
        assertEquals("lg(3)+lg(x/b)", Expression.valueOf("lg(3*x/b)").simplify().toString());
        assertEquals("-lg(7)+lg(15)", Expression.valueOf("lg(3*5/7)").simplify().toString());
    }
}
