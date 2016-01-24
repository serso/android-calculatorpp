package jscl.text;

import jscl.NumeralBase;
import jscl.math.Generic;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Digits implements Parser<String> {

    @Nonnull
    private final NumeralBase nb;

    public Digits(@Nonnull NumeralBase nb) {
        this.nb = nb;
    }

    // returns digit
    public String parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        final StringBuilder result = new StringBuilder();

        ParserUtils.skipWhitespaces(p);

        if (p.position.intValue() < p.expression.length() && nb.getAcceptableCharacters().contains(p.expression.charAt(p.position.intValue()))) {
            result.append(p.expression.charAt(p.position.intValue()));
            p.position.increment();
        } else {
            ParserUtils.throwParseException(p, pos0, Messages.msg_9);
        }

        while (p.position.intValue() < p.expression.length() && nb.getAcceptableCharacters().contains(p.expression.charAt(p.position.intValue()))) {
            result.append(p.expression.charAt(p.position.intValue()));
            p.position.increment();
        }

        return result.toString();
    }
}
