package jscl.text;

import jscl.math.Generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 2:44 PM
 */
class MinusParser implements Parser<Boolean> {

    public static final Parser<Boolean> parser = new MinusParser();

    private MinusParser() {
    }

    static boolean isMinus(char c) {
        return c == '-' || c == '−';
    }

    @Nonnull
    public Boolean parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) {
        final int pos0 = p.position.intValue();

        ParserUtils.skipWhitespaces(p);

        final int pos1 = p.position.intValue();
        if (pos1 < p.expression.length() && isMinus(p.expression.charAt(pos1))) {
            p.position.increment();
            return true;
        } else {
            p.position.setValue(pos0);
            return false;
        }
    }
}
