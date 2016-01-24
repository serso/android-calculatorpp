package jscl.text;

import jscl.math.Generic;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrimeCharacters implements Parser<Integer> {
    public static final Parser<Integer> parser = new PrimeCharacters();

    private PrimeCharacters() {
    }

    public Integer parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException {

        int pos0 = p.position.intValue();

        int result = 0;

        ParserUtils.skipWhitespaces(p);

        if (p.position.intValue() < p.expression.length() && p.expression.charAt(p.position.intValue()) == '\'') {
            p.position.increment();
            result = 1;
        } else {
            ParserUtils.throwParseException(p, pos0, Messages.msg_12, '\'');
        }

        while (p.position.intValue() < p.expression.length() && p.expression.charAt(p.position.intValue()) == '\'') {
            p.position.increment();
            result++;
        }

        return result;
    }
}
