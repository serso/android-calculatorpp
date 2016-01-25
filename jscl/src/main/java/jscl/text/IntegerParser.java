package jscl.text;

import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.NumeralBase;
import jscl.math.Generic;
import jscl.text.msg.Messages;

public class IntegerParser implements Parser<Integer> {

    public static final Parser<Integer> parser = new IntegerParser();

    private IntegerParser() {
    }

    public Integer parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        final NumeralBase nb = NumeralBaseParser.parser.parse(p, previousSumElement);

        ParserUtils.skipWhitespaces(p);
        final StringBuilder result;
        if (p.position.intValue() < p.expression.length() && nb.getAcceptableCharacters().contains(p.expression.charAt(p.position.intValue()))) {
            char c = p.expression.charAt(p.position.intValue());
            p.position.increment();
            result = new StringBuilder();
            result.append(c);
        } else {
            p.position.setValue(pos0);
            throw p.exceptionsPool.obtain(p.position.intValue(), p.expression, Messages.msg_7);
        }

        while (p.position.intValue() < p.expression.length() && nb.getAcceptableCharacters().contains(p.expression.charAt(p.position.intValue()))) {
            char c = p.expression.charAt(p.position.intValue());
            p.position.increment();
            result.append(c);
        }

        final String number = result.toString();
        try {
            return nb.toInteger(number);
        } catch (NumberFormatException e) {
            throw p.exceptionsPool.obtain(p.position.intValue(), p.expression, Messages.msg_8, Collections.singletonList(number));
        }
    }
}
