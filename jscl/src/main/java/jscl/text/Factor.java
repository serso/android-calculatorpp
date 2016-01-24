package jscl.text;

import jscl.math.Generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 2:45 PM
 */
class Factor implements Parser<Generic> {

    public static final Parser<Generic> parser = new Factor();

    private Factor() {
    }

    public Generic parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException {
        final boolean minus = MinusParser.parser.parse(p, previousSumElement);

        final Generic result = (Generic) UnsignedFactor.parser.parse(p, previousSumElement);

        return minus ? result.negate() : result;
    }
}
