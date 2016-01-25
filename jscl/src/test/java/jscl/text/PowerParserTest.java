package jscl.text;

import org.junit.Assert;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 3:45 PM
 */
public class PowerParserTest {

    @org.junit.Test
    public void testParse() throws Exception {
        PowerParser.parser.parse(Parser.Parameters.get("  ^"), null);
        PowerParser.parser.parse(Parser.Parameters.get(" **"), null);
        PowerParser.parser.parse(Parser.Parameters.get(" **7"), null);
        PowerParser.parser.parse(Parser.Parameters.get("^"), null);
        PowerParser.parser.parse(Parser.Parameters.get("**"), null);
        try {
            PowerParser.parser.parse(Parser.Parameters.get("*"), null);
            Assert.fail();
        } catch (ParseException e) {

        }
    }
}
