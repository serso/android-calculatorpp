package jscl.math.operator;

import jscl.JsclMathEngine;
import jscl.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PercentTest {

    @Test
    public void testNumeric() throws Exception {
        final JsclMathEngine me = new JsclMathEngine();

        assertEquals("150", me.evaluate("100+50%"));
        assertEquals("0", me.evaluate("100-100%"));
        assertEquals("50", me.evaluate("100*50%"));
        assertEquals("150", me.evaluate("100+100*50%"));
        assertEquals("125", me.evaluate("100+100*50%*50%"));
        assertEquals("125", me.evaluate("100+100*50%*(25+25)%"));
        assertEquals("250", me.evaluate("100+100*50%*(25+25)%+100%"));
        assertEquals("150", me.evaluate("100+(100*50%*(25+25)%+100%)"));
        assertEquals("140", me.evaluate("100+(20+20)%"));
        assertEquals("124", me.evaluate("100+(20%+20%)"));

        assertEquals("100+50%-50%", me.simplify("100+50%-50%"));

        assertEquals("100+(100*50%*(50)%+100%)", me.simplify("100+(100*50%*(25+25)%+100%)"));

        assertEquals("450", me.evaluate("((100+100*50%)+50%)*200%"));
        assertEquals("150", me.evaluate("((100+100*50%)*50%)+100%"));
        assertEquals("150", me.evaluate("100*50%+100"));
        assertEquals("75", me.evaluate("100+50%-50%"));
        assertEquals("75", me.evaluate("100+50%+(-50%)"));
        assertEquals("0", me.evaluate("0+(-50%)"));
        assertEquals("0", me.evaluate("0+(50%)"));
        assertEquals("0", me.evaluate("0+50%"));
        assertEquals("-150", me.evaluate("-100+50%"));
        assertEquals("-148.5", me.evaluate("1-100+50%"));
        assertEquals("-49.5", me.evaluate("1-100-50%"));
        assertEquals("-49.5", me.evaluate("(1-100)-50%"));
        assertEquals("-49", me.evaluate("1-(100-50%)"));
        assertEquals("50", me.evaluate("100-50%"));
        assertEquals("2600", me.evaluate("100+50%^2"));
        assertEquals("101.0813826568003", me.evaluate("100+50^2%"));
        assertEquals("22500", me.evaluate("(100+50%)^2"));
        assertEquals("225", me.evaluate("(100+50%)+50%"));
        assertEquals("225", me.evaluate("(100+50%)+(abs(-50)+10-10)%"));

        assertEquals("0", me.evaluate("100-(10+2*40+10)%"));
        assertEquals("3", me.evaluate("100-(10+2*40+10)%+3"));

        assertEquals("0", me.evaluate("100-(200/2)%"));
        assertEquals("3", me.evaluate("100-(200/2)%+3"));

        assertEquals("99", me.evaluate("100-2*50%"));
        assertEquals("102", me.evaluate("100-2*50%+3"));

        assertEquals("84", me.evaluate("20+2^3!"));
        assertEquals("21.0471285480509", me.evaluate("20+10^2%"));
        assertEquals("20.48", me.evaluate("20+4!*2%"));

        assertEquals("120", me.evaluate("100-20+50%"));

        try {
            me.evaluate("+50%");
            Assert.fail();
        } catch (ParseException e) {
        }

        assertEquals("0.5", me.evaluate("50%"));
        assertEquals("-0.5", me.evaluate("-50%"));
        assertEquals("225", me.evaluate("(100+50%)+50%"));

        // undefined behavior, percent function always uses preceding number ignoring multiplier after
        assertEquals("10100", me.evaluate("100+100%*100"));
    }
}
