package jscl;

import jscl.math.function.Constant;
import jscl.math.function.ExtendedConstant;
import jscl.math.function.IConstant;
import jscl.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NumeralBaseTest {

    @Test
    public void testEvaluation() throws Exception {
        MathEngine me = JsclMathEngine.getInstance();

        assertEquals("3", me.evaluate("0b:1+0b:10"));
        assertEquals("5", me.evaluate("0b:1+0b:100"));
        assertEquals("8", me.evaluate("0b:1+0b:100+(0b:1+0b:10)"));
        assertEquals("18", me.evaluate("0b:1+0b:100+(0b:1+0b:10)+10"));
        assertEquals("18.5", me.evaluate("0b:1+0b:100+(0b:1+0b:10)+10.5"));
        try {
            me.evaluate("0b:1+0b:100.+(0b:1+0b:10)+10.5");
            Assert.fail();
        } catch (ParseException e) {
        }

        try {
            me.evaluate("0b:1+0b:100E-2+(0b:1+0b:10)+10.5");
            Assert.fail();
        } catch (ParseException e) {
        }

        assertEquals("2748", me.evaluate("0x:ABC"));

        try {
            me.evaluate("0x:");
            Assert.fail();
        } catch (ParseException e) {
        }

        assertEquals("0", me.evaluate("0x:0"));

        IConstant constant = null;
        try {
            final ExtendedConstant.Builder a = new ExtendedConstant.Builder(new Constant("a"), 2d);
            constant = me.getConstantsRegistry().addOrUpdate(a.create());
            assertEquals("2748", me.evaluate("0x:ABC"));
            assertEquals("5496", me.evaluate("0x:ABC*a"));
            assertEquals("27480", me.evaluate("0x:ABC*0x:A"));
        } finally {
            if (constant != null) {
                final ExtendedConstant.Builder a = new ExtendedConstant.Builder(new Constant("a"), (String) null);
                me.getConstantsRegistry().addOrUpdate(a.create());
            }
        }
    }

    @Test
    public void testNumeralBases() throws Exception {
        MathEngine me = JsclMathEngine.getInstance();

        final NumeralBase defaultNumeralBase = me.getNumeralBase();
        try {
            me.setNumeralBase(NumeralBase.bin);
            assertEquals("∞", me.evaluate("∞"));
            assertEquals("-1011010+110101111.100001101011001*i", me.evaluate("asin(-1110100101)"));
            assertEquals("11", me.evaluate("0b:1+0b:10"));
            assertEquals("10", me.evaluate("0d:2"));
            assertEquals("11", me.evaluate("0d:3"));
            assertEquals("100", me.evaluate("0d:4"));
            assertEquals("11111111", me.evaluate("0d:255"));
            assertEquals("11", me.evaluate("1+10"));
            assertEquals("-1", me.evaluate("1-10"));
            assertEquals("11-i", me.evaluate("1+i+10-10*i"));
            assertEquals("11111110", me.evaluate("111001+11000101"));
            assertEquals("1101100100101111", me.evaluate("11011001001011110/10"));
            assertEquals("1001000011001010", me.evaluate("11011001001011110/11"));
            assertEquals("0.101010101010101", me.evaluate("10/11"));

            me.setNumeralBase(NumeralBase.hex);
            assertEquals("637B", me.evaluate("56CE+CAD"));
            assertEquals("637B", me.simplify("56CE+CAD"));

        } finally {
            me.setNumeralBase(defaultNumeralBase);
        }
    }
}
