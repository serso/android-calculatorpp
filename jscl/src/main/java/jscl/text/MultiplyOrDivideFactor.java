package jscl.text;

import jscl.math.Generic;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 2:45 PM
 */
class MultiplyOrDivideFactor implements Parser<Generic> {

    public static final Parser<Generic> multiply = new MultiplyOrDivideFactor(true);

    public static final Parser<Generic> divide = new MultiplyOrDivideFactor(false);

    boolean multiplication;

    private MultiplyOrDivideFactor(boolean multiplication) {
        this.multiplication = multiplication;
    }

    public Generic parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.getPosition().intValue();

        ParserUtils.skipWhitespaces(p);
        if (p.getPosition().intValue() < p.getExpression().length() && p.getExpression().charAt(p.getPosition().intValue()) == (multiplication ? '*' : '/')) {
            p.getPosition().increment();
        } else {
            ParserUtils.throwParseException(p, pos0, Messages.msg_10, '*', '/');
        }

        return ParserUtils.parseWithRollback(Factor.parser, pos0, previousSumElement, p);
    }
}
