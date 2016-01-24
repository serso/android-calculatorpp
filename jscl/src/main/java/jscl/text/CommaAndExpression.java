package jscl.text;

import jscl.math.Generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CommaAndExpression implements Parser<Generic> {

    public static final Parser<Generic> parser = new CommaAndExpression();

    private CommaAndExpression() {
    }

    public Generic parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        ParserUtils.skipWhitespaces(p);

        ParserUtils.tryToParse(p, pos0, ',');

        return ParserUtils.parseWithRollback(ExpressionParser.parser, pos0, previousSumElement, p);
    }
}
