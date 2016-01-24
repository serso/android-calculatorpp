package jscl.text;

import jscl.math.Generic;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 2:45 PM
 */
class DivideFactor implements Parser<Generic> {

    public static final Parser<Generic> parser = new DivideFactor();

    private DivideFactor() {
    }

    public Generic parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        final int pos0 = p.position.intValue();

        ParserUtils.skipWhitespaces(p);
        final int pos1 = p.position.intValue();
        if (pos1 < p.expression.length() && p.expression.charAt(pos1) == '/') {
            p.position.increment();
        } else {
            ParserUtils.throwParseException(p, pos0, Messages.msg_10, '*', '/');
        }

        return ParserUtils.parseWithRollback(Factor.parser, pos0, previousSumElement, p);
    }
}
