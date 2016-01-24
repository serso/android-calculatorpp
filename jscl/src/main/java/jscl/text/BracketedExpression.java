package jscl.text;

import jscl.math.ExpressionVariable;
import jscl.math.Generic;

import javax.annotation.Nonnull;

public class BracketedExpression implements Parser<ExpressionVariable> {

    public static final Parser<ExpressionVariable> parser = new BracketedExpression();

    private BracketedExpression() {
    }

    public ExpressionVariable parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        ParserUtils.tryToParse(p, pos0, '(');

        Generic result;
        try {
            result = ExpressionParser.parser.parse(p, previousSumElement);
        } catch (ParseException e) {
            p.position.setValue(pos0);
            throw e;
        }

        ParserUtils.tryToParse(p, pos0, ')');

        return new ExpressionVariable(result);
    }
}
