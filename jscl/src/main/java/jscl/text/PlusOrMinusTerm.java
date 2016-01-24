package jscl.text;

import jscl.math.Generic;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 2:44 PM
 */
class PlusOrMinusTerm implements Parser<Generic> {

    public static final Parser<Generic> parser = new PlusOrMinusTerm();

    private PlusOrMinusTerm() {
    }

    public Generic parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        final int pos0 = p.position.intValue();

        ParserUtils.skipWhitespaces(p);

        boolean minus = false;
        final int pos1 = p.position.intValue();
        if (pos1 < p.expression.length() && (p.expression.charAt(pos1) == '+' || MinusParser.isMinus(p.expression.charAt(pos1)))) {
            minus = MinusParser.isMinus(p.expression.charAt(pos1));
            p.position.increment();
        } else {
            ParserUtils.throwParseException(p, pos0, Messages.msg_10, '+', '-');
        }

        final Generic result = ParserUtils.parseWithRollback(TermParser.parser, pos0, previousSumElement, p);

        return minus ? result.negate() : result;
    }

}
