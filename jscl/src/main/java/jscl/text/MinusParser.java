package jscl.text;

import jscl.math.Generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 2:44 PM
 */
class MinusParser implements Parser<MinusParser.Result> {

    public static final Parser<Result> parser = new MinusParser();

    private MinusParser() {
    }

    @Nonnull
    public Result parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) {
        final boolean result;

        int pos0 = p.getPosition().intValue();

        ParserUtils.skipWhitespaces(p);

        if (p.getPosition().intValue() < p.getExpression().length() && p.getExpression().charAt(p.getPosition().intValue()) == '-') {
            result = true;
            p.getPosition().increment();
        } else {
            result = false;
            p.getPosition().setValue(pos0);
        }

        return new Result(result);
    }

    public static class Result {
        private final boolean sign;

        public Result(boolean sign) {
            this.sign = sign;
        }

        public boolean isSign() {
            return sign;
        }
    }
}
