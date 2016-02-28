package jscl.math.operator;

import jscl.JsclMathEngine;
import jscl.MathEngine;
import jscl.text.ParseException;
import jscl.text.msg.Messages;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DoubleFactorialTest {

    @Test
    public void testDoubleFactorial() throws Exception {
        final MathEngine me = JsclMathEngine.getInstance();

        assertEquals("1", me.evaluate("0!"));
        assertEquals("1", me.evaluate("1!"));
        assertEquals("2", me.evaluate("2!"));
        assertEquals("6", me.evaluate("3!"));
        assertEquals("24", me.evaluate("4!"));

        try {
            me.evaluate("(-1)!!");
            fail();
        } catch (ArithmeticException e) {
            // ok
        }

        assertEquals("-1", me.evaluate("-1!!"));
        assertEquals("1", me.evaluate("0!!"));
        assertEquals("1", me.evaluate("1!!"));
        assertEquals("2", me.evaluate("2!!"));
        assertEquals("2", me.evaluate("(2!!)!"));
        assertEquals("2", me.evaluate("(2!)!!"));
        assertEquals("3", me.evaluate("3!!"));
        assertEquals("48", me.evaluate("(3!)!!"));
        assertEquals("6", me.evaluate("(3!!)!"));
        assertEquals("8", me.evaluate("4!!"));
        assertEquals("15", me.evaluate("5!!"));
        assertEquals("48", me.evaluate("6!!"));
        assertEquals("105", me.evaluate("7!!"));
        assertEquals("384", me.evaluate("8!!"));
        assertEquals("945", me.evaluate("9!!"));

        try {
            me.evaluate("9!!!");
            fail();
        } catch (ParseException e) {
            if (Messages.msg_18.equals(e.getMessageCode())) {
                // ok
            } else {
                fail();
            }
        }


    }
}
