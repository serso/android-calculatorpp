package jscl.math.operator;

import jscl.JsclMathEngine;
import jscl.text.ParseException;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 11/14/11
 * Time: 2:10 PM
 */
public class PercentTest {

    @Test
    public void testNumeric() throws Exception {
        final JsclMathEngine mathEngine = JsclMathEngine.getInstance();

        Assert.assertEquals("150", mathEngine.evaluate("100+50%"));
        Assert.assertEquals("0", mathEngine.evaluate("100-100%"));
        Assert.assertEquals("50", mathEngine.evaluate("100*50%"));
        Assert.assertEquals("150", mathEngine.evaluate("100+100*50%"));
        Assert.assertEquals("125", mathEngine.evaluate("100+100*50%*50%"));
        Assert.assertEquals("125", mathEngine.evaluate("100+100*50%*(25+25)%"));
        Assert.assertEquals("250", mathEngine.evaluate("100+100*50%*(25+25)%+100%"));
        Assert.assertEquals("150", mathEngine.evaluate("100+(100*50%*(25+25)%+100%)"));
        Assert.assertEquals("140", mathEngine.evaluate("100+(20+20)%"));
        // todo serso: think about such behaviour
        Assert.assertEquals("124", mathEngine.evaluate("100+(20%+20%)"));

        Assert.assertEquals("100+50%-50%", mathEngine.simplify("100+50%-50%"));

        Assert.assertEquals("100+(100*50%*(50)%+100%)", mathEngine.simplify("100+(100*50%*(25+25)%+100%)"));


        Assert.assertEquals("450", mathEngine.evaluate("((100+100*50%)+50%)*200%"));
        Assert.assertEquals("150", mathEngine.evaluate("((100+100*50%)*50%)+100%"));
        Assert.assertEquals("150", mathEngine.evaluate("100*50%+100"));
        Assert.assertEquals("75", mathEngine.evaluate("100+50%-50%"));
        Assert.assertEquals("75", mathEngine.evaluate("100+50%+(-50%)"));
        Assert.assertEquals("0", mathEngine.evaluate("0+(-50%)"));
        Assert.assertEquals("0", mathEngine.evaluate("0+(50%)"));
        Assert.assertEquals("0", mathEngine.evaluate("0+50%"));
        Assert.assertEquals("-150", mathEngine.evaluate("-100+50%"));
        Assert.assertEquals("-148.5", mathEngine.evaluate("1-100+50%"));
        Assert.assertEquals("-49.5", mathEngine.evaluate("1-100-50%"));
        Assert.assertEquals("-49.5", mathEngine.evaluate("(1-100)-50%"));
        Assert.assertEquals("-49", mathEngine.evaluate("1-(100-50%)"));
        Assert.assertEquals("50", mathEngine.evaluate("100-50%"));
        Assert.assertEquals("2600", mathEngine.evaluate("100+50%^2"));
        Assert.assertEquals("101.0813826568003", mathEngine.evaluate("100+50^2%"));
        Assert.assertEquals("22500", mathEngine.evaluate("(100+50%)^2"));
        Assert.assertEquals("225", mathEngine.evaluate("(100+50%)+50%"));
        Assert.assertEquals("225", mathEngine.evaluate("(100+50%)+(abs(-50)+10-10)%"));

        Assert.assertEquals("0", mathEngine.evaluate("100-(10+2*40+10)%"));
        Assert.assertEquals("3", mathEngine.evaluate("100-(10+2*40+10)%+3"));

        Assert.assertEquals("0", mathEngine.evaluate("100-(200/2)%"));
        Assert.assertEquals("3", mathEngine.evaluate("100-(200/2)%+3"));

        Assert.assertEquals("99", mathEngine.evaluate("100-2*50%"));
        Assert.assertEquals("102", mathEngine.evaluate("100-2*50%+3"));

        Assert.assertEquals("84", mathEngine.evaluate("20+2^3!"));
        Assert.assertEquals("21.0471285480509", mathEngine.evaluate("20+10^2%"));
        Assert.assertEquals("20.48", mathEngine.evaluate("20+4!*2%"));

        Assert.assertEquals("120", mathEngine.evaluate("100-20+50%"));

        try {
            mathEngine.evaluate("+50%");
            Assert.fail();
        } catch (ParseException e) {
        }

        Assert.assertEquals("0.5", mathEngine.evaluate("50%"));
        Assert.assertEquals("-0.5", mathEngine.evaluate("-50%"));
        Assert.assertEquals("225", mathEngine.evaluate("(100+50%)+50%"));

    }
}
