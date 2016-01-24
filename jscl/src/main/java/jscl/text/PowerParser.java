package jscl.text;

import jscl.math.Generic;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 2:45 PM
 */
class PowerParser implements Parser<Void> {

    public static final Parser<Void> parser = new PowerParser();

    private PowerParser() {
    }

    @Nullable
    public Void parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        ParserUtils.skipWhitespaces(p);

        if (p.position.intValue() < p.expression.length() && p.expression.charAt(p.position.intValue()) == '^') {
            p.position.increment();
        } else {
            if (isDoubleStar(p.expression, p.position)) {
                p.position.increment();
                p.position.increment();
            } else {
                ParserUtils.throwParseException(p, pos0, Messages.msg_10, '^', "**");
            }
        }

        return null;
    }

    private boolean isDoubleStar(@Nonnull String string, @Nonnull MutableInt position) {
        final int i = position.intValue();
        return i < string.length() && i + 1 < string.length() && string.charAt(i) == '*' && string.charAt(i + 1) == '*';
    }
}
