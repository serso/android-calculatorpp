package jscl.math.function;

import jscl.AngleUnit;
import jscl.CustomFunctionCalculationException;
import jscl.JsclMathEngine;
import jscl.NumeralBase;
import jscl.math.Expression;
import jscl.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * User: serso
 * Date: 11/15/11
 * Time: 5:35 PM
 */
public class CustomFunctionTest {

    @Test
    public void testLog() throws Exception {
        JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        assertEquals("∞", Expression.valueOf("1/0").numeric().toString());
        assertEquals("∞", Expression.valueOf("ln(10)/ln(1)").numeric().toString());

        // logarithm
        final CustomFunction.Builder jBuilder = new CustomFunction.Builder(true, "log", asList("a", "b"), "ln(b)/ln(a)");
        Function function = mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder.create());
        assertEquals("log(a, b)", function.toString());
        assertEquals("ln(b)/ln(a)", ((CustomFunction) mathEngine.getFunctionsRegistry().get("log")).getContent());
        assertEquals("∞", Expression.valueOf("log(1, 10)").numeric().toString());
        assertEquals("3.321928094887363", Expression.valueOf("log(2, 10)").numeric().toString());
        assertEquals("1.430676558073393", Expression.valueOf("log(5, 10)").numeric().toString());
        assertEquals("0.9602525677891275", Expression.valueOf("log(11, 10)").numeric().toString());
        assertEquals("1/b*1/ln(a)", Expression.valueOf("∂(log(a, b), b)").expand().toString());
        assertEquals("-1/a*(1/ln(a))^2*ln(b)", Expression.valueOf("∂(log(a, b), a)").expand().toString());

    }

    @Test
    public void testDerivative() throws Exception {
        JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        final CustomFunction.Builder jBuilder = new CustomFunction.Builder("t1", asList("a"), "sin(a)");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder.create());
        assertEquals("1", Expression.valueOf("t1(90)").numeric().toString());
        assertEquals("cos(t)", Expression.valueOf("∂(t1(t), t)").expand().toString());
        assertEquals("0", Expression.valueOf("∂(t1(t), t2)").expand().toString());
        assertEquals("cos(a)", Expression.valueOf("∂(t1(a), a)").expand().toString());
        assertEquals("1", Expression.valueOf("∂(t1(a), t1(a))").expand().toString());
        final CustomFunction.Builder jBuilder1 = new CustomFunction.Builder("t2", asList("a", "b"), "b*sin(a)");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder1.create());
        assertEquals("y*cos(x)", Expression.valueOf("∂(t2(x, y), x)").expand().toString());
        assertEquals("sin(x)", Expression.valueOf("∂(t2(x, y), y)").expand().toString());
    }

    @Test
    public void testAntiDerivative() throws Exception {
        JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        final CustomFunction.Builder jBuilder = new CustomFunction.Builder("t1", asList("a"), "sin(a)");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder.create());
        assertEquals("1", Expression.valueOf("t1(90)").numeric().toString());


        try {
            mathEngine.setAngleUnits(AngleUnit.rad);
            assertEquals("-cos(t)", Expression.valueOf("∫(t1(t), t)").expand().toString());
            assertEquals("t2*sin(t)", Expression.valueOf("∫(t1(t), t2)").expand().toString());
            assertEquals("-cos(a)", Expression.valueOf("∫(t1(a), a)").expand().toString());
            assertEquals("1/2*sin(a)^2", Expression.valueOf("∫(t1(a), t1(a))").expand().toString());
            final CustomFunction.Builder jBuilder1 = new CustomFunction.Builder("t2", asList("a", "b"), "b*sin(a)");
            mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder1.create());
            assertEquals("-y*cos(x)", Expression.valueOf("∫(t2(x, y), x)").expand().toString());
            assertEquals("1/2*y^2*sin(x)", Expression.valueOf("∫(t2(x, y), y)").expand().toString());
        } finally {
            mathEngine.setAngleUnits(AngleUnit.deg);
        }
    }


    @Test
    public void testFunction() throws Exception {
        JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        final CustomFunction.Builder jBuilder = new CustomFunction.Builder("testFunction", asList("a", "b", "c", "d"), "b*cos(a)/c+d");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder.create());
        assertEquals("6.749543120264322", Expression.valueOf("testFunction(2, 3, 4, 6)").numeric().toString());
        assertEquals("7.749543120264322", Expression.valueOf("testFunction(2, 3, 4, 7)").numeric().toString());
        assertEquals("6.749543120264322", Expression.valueOf("testFunction(2*1, 3, 4, 6)").numeric().toString());
        assertEquals("6.749543120264322", Expression.valueOf("testFunction(2*1, 3, 4, 3!)").numeric().toString());
        assertEquals("6.749543120264322", Expression.valueOf("testFunction(2*1, 3, 2^2-1+e^0, 3!)").numeric().toString());
        assertEquals("testFunction(2, 3, 4, 3!)", Expression.valueOf("testFunction(2*1, 3, 2^2-1+e^0, 3!)").simplify().toString());
        assertEquals("3*cos(2)/4+3!", Expression.valueOf("testFunction(2*1, 3, 2^2-1+e^0, 3!)").expand().toString());
        assertEquals("3*(1/2*1/exp(2*i)+1/2*exp(2*i))/4+3!", Expression.valueOf("testFunction(2*1, 3, 2^2-1+e^0, 3!)").elementary().toString());
        assertEquals("sin(t)^2*testFunction(2, 3, 4, 3!)", Expression.valueOf("sin(t)*testFunction(2*1, 3, 2^2-1+e^0, 3!)*sin(t)").simplify().toString());
        assertEquals("testFunction(2, 3, 4, 3!)^2", Expression.valueOf("testFunction(2*1, 3, 2^2-1+e^0, 3!)*testFunction(2, 3, 4, 3!)").simplify().toString());
        try {
            Expression.valueOf("testFunction(2*1, 3, 2^2-1+e^0, 3!)*testFunction(2, 3, 4)");
            Assert.fail();
        } catch (ParseException e) {
            // ok, not enough parameters
        }

        final ExtendedConstant.Builder a = new ExtendedConstant.Builder(new Constant("a"), 1000d);
        mathEngine.getConstantsRegistry().addOrUpdate(a.create());
        final CustomFunction.Builder jBuilder1 = new CustomFunction.Builder("testFunction2", asList("a", "b", "c", "d"), "b*cos(a)/c+d");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder1.create());
        assertEquals("6.749543120264322", Expression.valueOf("testFunction2(2, 3, 4, 6)").numeric().toString());
        assertEquals("7.749543120264322", Expression.valueOf("testFunction2(2, 3, 4, 7)").numeric().toString());
        assertEquals("6.749543120264322", Expression.valueOf("testFunction2(2*1, 3, 4, 6)").numeric().toString());
        assertEquals("6.749543120264322", Expression.valueOf("testFunction2(2*1, 3, 2^2-1+e^0, 3!)").numeric().toString());

        final CustomFunction.Builder jBuilder2 = new CustomFunction.Builder("testFunction3", asList("a", "b", "c", "d"), "testFunction2(a, b, c, d) - testFunction(a, b, c, d)");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder2.create());
        assertEquals("0", Expression.valueOf("testFunction3(2, 3, 4, 6)").numeric().toString());
        assertEquals("0", Expression.valueOf("testFunction3(2, 3, 4, 7)").numeric().toString());
        assertEquals("0", Expression.valueOf("testFunction3(2*1, 3, 4, 6)").numeric().toString());
        assertEquals("0", Expression.valueOf("testFunction3(2*1, 3, 2^2-1+e^0, 3!)").numeric().toString());

        final CustomFunction.Builder jBuilder3 = new CustomFunction.Builder("testFunction4", asList("a", "b", "c", "d"), "testFunction2(a, b/2, c/3, d/4) - testFunction(a, b!, c, d)");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder3.create());
        assertEquals("-4.874771560132161", Expression.valueOf("testFunction4(2, 3, 4, 6)").numeric().toString());
        assertEquals("-5.624771560132161", Expression.valueOf("testFunction4(2, 3, 4, 7)").numeric().toString());
        assertEquals("-4.874771560132161", Expression.valueOf("testFunction4(2*1, 3, 4, 6)").numeric().toString());
        assertEquals("-4.874771560132161", Expression.valueOf("testFunction4(2*1, 3, 2^2-1+e^0, 3!)").numeric().toString());

        final CustomFunction.Builder jBuilder4 = new CustomFunction.Builder("testFunction5", asList("a", "b"), "testFunction2(a, b/2, 2, 1) - testFunction(a, b!, 4!, 1)");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder4.create());
        assertEquals("0.4996954135095478", Expression.valueOf("testFunction5(2, 3)").numeric().toString());
        assertEquals("0.4996954135095478", Expression.valueOf("testFunction5(2, 3)").numeric().toString());
        assertEquals("0.4996954135095478", Expression.valueOf("testFunction5(2*1, 3)").numeric().toString());
        assertEquals("-0.0000000000000001", Expression.valueOf("testFunction5(2*1, 2^2-1+e^0)").numeric().toString());

        try {
            Expression.valueOf("testFunction5(2, 3.5)").numeric();
            Assert.fail();
        } catch (ArithmeticException e) {

        }

        final CustomFunction.Builder jBuilder5 = new CustomFunction.Builder("testFunction6", asList("a", "b"), "testFunction(a, b!, 4!, Π)");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder5.create());
        assertEquals("180.2498477067548", Expression.valueOf("testFunction6(2, 3)").numeric().toString());

        final ExtendedConstant.Builder e = new ExtendedConstant.Builder(new Constant("e"), 181d);
        mathEngine.getConstantsRegistry().addOrUpdate(e.create());
        final CustomFunction.Builder jBuilder6 = new CustomFunction.Builder("testFunction7", asList("a", "b"), "testFunction(a, b!, 4!, e)");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder6.create());
        assertEquals("181.2498477067548", Expression.valueOf("testFunction7(2, 3)").numeric().toString());

        final ExtendedConstant.Builder e1 = new ExtendedConstant.Builder(new Constant("e"), 181d);
        mathEngine.getConstantsRegistry().addOrUpdate(e1.create());
        final CustomFunction.Builder jBuilder7 = new CustomFunction.Builder("testFunction8", asList("a", "b"), "testFunction(sin(a), b!, 4!, e)");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder7.create());
        assertEquals("181.249999953623", Expression.valueOf("testFunction8(2, 3)").numeric().toString());

    }

    @Test
    public void testFunction2() throws Exception {
        JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        final CustomFunction.Builder jBuilder = new CustomFunction.Builder("f", asList("x", "y"), "z1/z2*√(x^2+y^2)");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder.create());
        final CustomFunction.Builder jBuilder1 = new CustomFunction.Builder("f2", asList("x", "y"), "√(x^2+y^2)");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder1.create());
        final CustomFunction.Builder jBuilder2 = new CustomFunction.Builder("f3", asList("x", "y"), "x^2+y^2");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder2.create());

        try {
            assertEquals("1", Expression.valueOf("f(1, 1)").numeric().toString());
            Assert.fail();
        } catch (ArithmeticException e) {
            //ok
        }

        assertEquals("1.414213562373095", Expression.valueOf("f2(1, 1)").numeric().toString());
        assertEquals("5", Expression.valueOf("f2(4, 3)").numeric().toString());

        assertEquals("2*z1", Expression.valueOf("∂(f3(z1, z2), z1)").expand().toString());
        assertEquals("2*z2", Expression.valueOf("∂(f3(z1, z2), z2)").expand().toString());

        // test symbols
        final CustomFunction.Builder jBuilder3 = new CustomFunction.Builder("f4", asList("x", "y"), "2 000*x^2+y^2");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder3.create());
        final CustomFunction.Builder jBuilder4 = new CustomFunction.Builder("f5", asList("x", "y"), "2'000* x ^2+y^2\r");
        mathEngine.getFunctionsRegistry().addOrUpdate(jBuilder4.create());
    }

    @Test
    public void testNumbersAreReadInBinMode() throws Exception {
        final JsclMathEngine me = JsclMathEngine.getInstance();
        me.setNumeralBase(NumeralBase.bin);
        final CustomFunction f = new CustomFunction.Builder("test", asList("x", "y"), "2000*x-0.001*y").create();
        assertEquals(NumeralBase.bin, me.getNumeralBase());
        me.setNumeralBase(NumeralBase.dec);
        assertEquals("2000*x-0.001*y", f.getContent());
    }

    @Test
    public void testInvalidFunctionShouldReturnNumeralBase() throws Exception {
        final JsclMathEngine me = JsclMathEngine.getInstance();
        me.setNumeralBase(NumeralBase.bin);
        try {
            new CustomFunction.Builder("test", Collections.<String>emptyList(), "2000*").create();
            fail();
        } catch (CustomFunctionCalculationException ignored) {
        }
        assertEquals(NumeralBase.bin, me.getNumeralBase());
        me.setNumeralBase(NumeralBase.dec);
    }
}
