package jscl.math.operator;

import jscl.math.Expression;
import jscl.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 12/26/11
 * Time: 9:56 AM
 */
public class RandTest {

    @Test
    public void testRand() throws Exception {
        /*testRandString("rand()-rand()");
          testRandString("rand()*rand()");
          testRandString("rand()^2");
          testRandString("rand()/rand()");*/
    }

    private void testRandString(final String expression) throws ParseException {
        Assert.assertEquals(expression, Expression.valueOf(expression).toString());
    }
}
