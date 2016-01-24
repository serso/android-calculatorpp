package jscl.math.operator;

import jscl.JsclMathEngine;
import org.junit.Test;

/**
 * User: serso
 * Date: 12/23/11
 * Time: 4:52 PM
 */
public class GcdTest {
    @Test
    public void testNumeric() throws Exception {
        JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        //mathEngine.getOperatorsRegistry().add(new Gcd());


        //Assert.assertEquals("1", Expression.valueOf("gcd(1, 1)").numeric().toString());
        //Assert.assertEquals("1", Expression.valueOf("gcd(2, 1)").numeric().toString());
        //Assert.assertEquals("2", Expression.valueOf("gcd(2, 4)").numeric().toString());
        //Assert.assertEquals("4", Expression.valueOf("gcd(4, 8)").numeric().toString());
        //Assert.assertEquals("4", Expression.valueOf("gcd(4.0, 8.0)").numeric().toString());
        //Assert.assertEquals("4", Expression.valueOf("gcd(8, 4)").numeric().toString());
    }
}
