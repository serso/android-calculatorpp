package jscl.math;

import jscl.math.numeric.Real;
import org.junit.Test;

/**
 * User: serso
 * Date: 12/23/11
 * Time: 5:28 PM
 */
public class LiteralTest {
    @Test
    public void testGcd() throws Exception {

    }

    @Test
    public void testScm() throws Exception {
        Expression e1 = Expression.valueOf("2+sin(2)");
        Expression e2 = Expression.valueOf("3+cos(2)");
        Literal l1 = Literal.valueOf(new DoubleVariable(new NumericWrapper(Real.valueOf(2d))));
        Literal l2 = Literal.valueOf(new DoubleVariable(new NumericWrapper(Real.valueOf(4d))));

        System.out.println(e1);
        System.out.println(e2);

        Literal result = Literal.newInstance();
        System.out.println(-1 + " -> " + result);
        for (int i = 0; i < e1.size(); i++) {
            result = result.scm(e1.literal(i));
            System.out.println(i + " -> " + result);
        }

        System.out.println(e1.literalScm());
        System.out.println(e2.literalScm());

    }
}
