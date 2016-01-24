package jscl.text;

import jscl.JsclMathEngine;
import org.junit.Assert;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 3:45 PM
 */
public class PowerParserTest {

    @org.junit.Test
    public void testParse() throws Exception {
        PowerParser.parser.parse(Parser.Parameters.newInstance("  ^", new MutableInt(0), JsclMathEngine.getInstance()), null);
        PowerParser.parser.parse(Parser.Parameters.newInstance(" **", new MutableInt(0), JsclMathEngine.getInstance()), null);
        PowerParser.parser.parse(Parser.Parameters.newInstance(" **7", new MutableInt(0), JsclMathEngine.getInstance()), null);
        PowerParser.parser.parse(Parser.Parameters.newInstance("^", new MutableInt(0), JsclMathEngine.getInstance()), null);
        PowerParser.parser.parse(Parser.Parameters.newInstance("**", new MutableInt(0), JsclMathEngine.getInstance()), null);
        try {
            PowerParser.parser.parse(Parser.Parameters.newInstance("*", new MutableInt(0), JsclMathEngine.getInstance()), null);
            Assert.fail();
        } catch (ParseException e) {

        }
    }
}
