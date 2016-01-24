package jscl.text;

import jscl.math.Generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExpressionParser implements Parser<Generic> {

    public static final Parser<Generic> parser = new ExpressionParser();

    private ExpressionParser() {
    }

    public Generic parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException {

        boolean sign = MinusParser.parser.parse(p, previousSumElement).isSign();

        Generic result = TermParser.parser.parse(p, previousSumElement);

        if (sign) {
            result = result.negate();
        }

        while (true) {
            try {
                result = result.add(PlusOrMinusTerm.parser.parse(p, result));
            } catch (ParseException e) {
                break;
            }
        }

        return result;
    }
}

