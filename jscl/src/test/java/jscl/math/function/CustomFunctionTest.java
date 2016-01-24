package jscl.math.function;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.math.Expression;
import jscl.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * User: serso
 * Date: 11/15/11
 * Time: 5:35 PM
 */
public class CustomFunctionTest {

    @Test
    public void testLog() throws Exception {
        JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        Assert.assertEquals("∞", Expression.valueOf("1/0").numeric().toString());
        Assert.assertEquals("∞", Expression.valueOf("ln(10)/ln(1)").numeric().toString());

        // logarithm
        Function function = mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder(true, "log", Arrays.asList("a", "b"), "ln(b)/ln(a)"));
        Assert.assertEquals("log(a, b)", function.toString());
        Assert.assertEquals("ln(b)/ln(a)", ((CustomFunction) mathEngine.getFunctionsRegistry().get("log")).getContent());
        Assert.assertEquals("∞", Expression.valueOf("log(1, 10)").numeric().toString());
        Assert.assertEquals("3.3219280948873626", Expression.valueOf("log(2, 10)").numeric().toString());
        Assert.assertEquals("1.4306765580733933", Expression.valueOf("log(5, 10)").numeric().toString());
        Assert.assertEquals("0.9602525677891275", Expression.valueOf("log(11, 10)").numeric().toString());
        Assert.assertEquals("1/b*1/ln(a)", Expression.valueOf("∂(log(a, b), b)").expand().toString());
        Assert.assertEquals("-1/a*(1/ln(a))^2*ln(b)", Expression.valueOf("∂(log(a, b), a)").expand().toString());

    }

    @Test
    public void testDerivative() throws Exception {
        JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("t1", Arrays.asList("a"), "sin(a)"));
        Assert.assertEquals("1", Expression.valueOf("t1(90)").numeric().toString());
        Assert.assertEquals("cos(t)", Expression.valueOf("∂(t1(t), t)").expand().toString());
        Assert.assertEquals("0", Expression.valueOf("∂(t1(t), t2)").expand().toString());
        Assert.assertEquals("cos(a)", Expression.valueOf("∂(t1(a), a)").expand().toString());
        Assert.assertEquals("1", Expression.valueOf("∂(t1(a), t1(a))").expand().toString());
        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("t2", Arrays.asList("a", "b"), "b*sin(a)"));
        Assert.assertEquals("y*cos(x)", Expression.valueOf("∂(t2(x, y), x)").expand().toString());
        Assert.assertEquals("sin(x)", Expression.valueOf("∂(t2(x, y), y)").expand().toString());
    }

    @Test
    public void testAntiDerivative() throws Exception {
        JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("t1", Arrays.asList("a"), "sin(a)"));
        Assert.assertEquals("1", Expression.valueOf("t1(90)").numeric().toString());


        try {
            mathEngine.setAngleUnits(AngleUnit.rad);
            Assert.assertEquals("-cos(t)", Expression.valueOf("∫(t1(t), t)").expand().toString());
            Assert.assertEquals("t2*sin(t)", Expression.valueOf("∫(t1(t), t2)").expand().toString());
            Assert.assertEquals("-cos(a)", Expression.valueOf("∫(t1(a), a)").expand().toString());
            Assert.assertEquals("1/2*sin(a)^2", Expression.valueOf("∫(t1(a), t1(a))").expand().toString());
            mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("t2", Arrays.asList("a", "b"), "b*sin(a)"));
            Assert.assertEquals("-y*cos(x)", Expression.valueOf("∫(t2(x, y), x)").expand().toString());
            Assert.assertEquals("1/2*y^2*sin(x)", Expression.valueOf("∫(t2(x, y), y)").expand().toString());
        } finally {
            mathEngine.setAngleUnits(AngleUnit.deg);
        }
    }


    @Test
    public void testFunction() throws Exception {
        JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("testFunction", Arrays.asList("a", "b", "c", "d"), "b*cos(a)/c+d"));
        Assert.assertEquals("6.749543120264322", Expression.valueOf("testFunction(2, 3, 4, 6)").numeric().toString());
        Assert.assertEquals("7.749543120264322", Expression.valueOf("testFunction(2, 3, 4, 7)").numeric().toString());
        Assert.assertEquals("6.749543120264322", Expression.valueOf("testFunction(2*1, 3, 4, 6)").numeric().toString());
        Assert.assertEquals("6.749543120264322", Expression.valueOf("testFunction(2*1, 3, 4, 3!)").numeric().toString());
        Assert.assertEquals("6.749543120264322", Expression.valueOf("testFunction(2*1, 3, 2^2-1+e^0, 3!)").numeric().toString());
        Assert.assertEquals("testFunction(2, 3, 4, 3!)", Expression.valueOf("testFunction(2*1, 3, 2^2-1+e^0, 3!)").simplify().toString());
        Assert.assertEquals("3*cos(2)/4+3!", Expression.valueOf("testFunction(2*1, 3, 2^2-1+e^0, 3!)").expand().toString());
        Assert.assertEquals("3*(1/2*1/exp(2*i)+1/2*exp(2*i))/4+3!", Expression.valueOf("testFunction(2*1, 3, 2^2-1+e^0, 3!)").elementary().toString());
        Assert.assertEquals("sin(t)^2*testFunction(2, 3, 4, 3!)", Expression.valueOf("sin(t)*testFunction(2*1, 3, 2^2-1+e^0, 3!)*sin(t)").simplify().toString());
        Assert.assertEquals("testFunction(2, 3, 4, 3!)^2", Expression.valueOf("testFunction(2*1, 3, 2^2-1+e^0, 3!)*testFunction(2, 3, 4, 3!)").simplify().toString());
        try {
            Expression.valueOf("testFunction(2*1, 3, 2^2-1+e^0, 3!)*testFunction(2, 3, 4)");
            Assert.fail();
        } catch (ParseException e) {
            // ok, not enough parameters
        }

        mathEngine.getConstantsRegistry().add(new ExtendedConstant.Builder(new Constant("a"), 1000d));
        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("testFunction2", Arrays.asList("a", "b", "c", "d"), "b*cos(a)/c+d"));
        Assert.assertEquals("6.749543120264322", Expression.valueOf("testFunction2(2, 3, 4, 6)").numeric().toString());
        Assert.assertEquals("7.749543120264322", Expression.valueOf("testFunction2(2, 3, 4, 7)").numeric().toString());
        Assert.assertEquals("6.749543120264322", Expression.valueOf("testFunction2(2*1, 3, 4, 6)").numeric().toString());
        Assert.assertEquals("6.749543120264322", Expression.valueOf("testFunction2(2*1, 3, 2^2-1+e^0, 3!)").numeric().toString());

        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("testFunction3", Arrays.asList("a", "b", "c", "d"), "testFunction2(a, b, c, d) - testFunction(a, b, c, d)"));
        Assert.assertEquals("0", Expression.valueOf("testFunction3(2, 3, 4, 6)").numeric().toString());
        Assert.assertEquals("0", Expression.valueOf("testFunction3(2, 3, 4, 7)").numeric().toString());
        Assert.assertEquals("0", Expression.valueOf("testFunction3(2*1, 3, 4, 6)").numeric().toString());
        Assert.assertEquals("0", Expression.valueOf("testFunction3(2*1, 3, 2^2-1+e^0, 3!)").numeric().toString());

        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("testFunction4", Arrays.asList("a", "b", "c", "d"), "testFunction2(a, b/2, c/3, d/4) - testFunction(a, b!, c, d)"));
        Assert.assertEquals("-4.874771560132161", Expression.valueOf("testFunction4(2, 3, 4, 6)").numeric().toString());
        Assert.assertEquals("-5.624771560132161", Expression.valueOf("testFunction4(2, 3, 4, 7)").numeric().toString());
        Assert.assertEquals("-4.874771560132161", Expression.valueOf("testFunction4(2*1, 3, 4, 6)").numeric().toString());
        Assert.assertEquals("-4.874771560132161", Expression.valueOf("testFunction4(2*1, 3, 2^2-1+e^0, 3!)").numeric().toString());

        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("testFunction5", Arrays.asList("a", "b"), "testFunction2(a, b/2, 2, 1) - testFunction(a, b!, 4!, 1)"));
        Assert.assertEquals("0.4996954135095478", Expression.valueOf("testFunction5(2, 3)").numeric().toString());
        Assert.assertEquals("0.4996954135095478", Expression.valueOf("testFunction5(2, 3)").numeric().toString());
        Assert.assertEquals("0.4996954135095478", Expression.valueOf("testFunction5(2*1, 3)").numeric().toString());
        Assert.assertEquals("-111.02230246251565E-18", Expression.valueOf("testFunction5(2*1, 2^2-1+e^0)").numeric().toString());

        try {
            Expression.valueOf("testFunction5(2, 3.5)").numeric();
            Assert.fail();
        } catch (ArithmeticException e) {

        }

        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("testFunction6", Arrays.asList("a", "b"), "testFunction(a, b!, 4!, Π)"));
        Assert.assertEquals("180.24984770675476", Expression.valueOf("testFunction6(2, 3)").numeric().toString());

        mathEngine.getConstantsRegistry().add(new ExtendedConstant.Builder(new Constant("e"), 181d));
        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("testFunction7", Arrays.asList("a", "b"), "testFunction(a, b!, 4!, e)"));
        Assert.assertEquals("181.24984770675476", Expression.valueOf("testFunction7(2, 3)").numeric().toString());

        mathEngine.getConstantsRegistry().add(new ExtendedConstant.Builder(new Constant("e"), 181d));
        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("testFunction8", Arrays.asList("a", "b"), "testFunction(sin(a), b!, 4!, e)"));
        Assert.assertEquals("181.24999995362296", Expression.valueOf("testFunction8(2, 3)").numeric().toString());

    }

    @Test
    public void testFunction2() throws Exception {
        JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("f", Arrays.asList("x", "y"), "z1/z2*√(x^2+y^2)"));
        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("f2", Arrays.asList("x", "y"), "√(x^2+y^2)"));
        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("f3", Arrays.asList("x", "y"), "x^2+y^2"));

        try {
            Assert.assertEquals("1", Expression.valueOf("f(1, 1)").numeric().toString());
            Assert.fail();
        } catch (ArithmeticException e) {
            //ok
        }

        Assert.assertEquals("1.4142135623730951", Expression.valueOf("f2(1, 1)").numeric().toString());
        Assert.assertEquals("5", Expression.valueOf("f2(4, 3)").numeric().toString());

        Assert.assertEquals("2*z1", Expression.valueOf("∂(f3(z1, z2), z1)").expand().toString());
        Assert.assertEquals("2*z2", Expression.valueOf("∂(f3(z1, z2), z2)").expand().toString());

        // test symbols
        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("f4", Arrays.asList("x", "y"), "2 000*x^2+y^2"));
        mathEngine.getFunctionsRegistry().add(new CustomFunction.Builder("f5", Arrays.asList("x", "y"), "2'000* x ^2+y^2\r"));


    }
}
