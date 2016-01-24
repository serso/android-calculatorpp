package jscl.text;

import jscl.math.Generic;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 2:45 PM
 */
class UnsignedExponent implements Parser<Generic> {

    public static final Parser<Generic> parser = new UnsignedExponent();

    private UnsignedExponent() {
    }

    public Generic parse(@Nonnull Parameters p, final Generic previousSumElement) throws ParseException {
        final Generic content = PrimaryExpressionParser.parser.parse(p, previousSumElement);
        return new PostfixFunctionsParser(content).parse(p, previousSumElement);
    }
}
