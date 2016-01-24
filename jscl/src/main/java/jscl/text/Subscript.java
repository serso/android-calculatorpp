package jscl.text;

import jscl.math.Generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Subscript implements Parser<Generic> {

    public static final Parser<Generic> parser = new Subscript();

    private Subscript() {
    }

    public Generic parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException {
        int pos0 = p.getPosition().intValue();

        ParserUtils.tryToParse(p, pos0, '[');

        Generic a;
        try {
            a = ExpressionParser.parser.parse(p, previousSumElement);
        } catch (ParseException e) {
            p.getPosition().setValue(pos0);
            throw e;
        }

        ParserUtils.tryToParse(p, pos0, ']');

        return a;
    }

}
