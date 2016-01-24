package jscl.text;

import jscl.math.Generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 2:45 PM
 */
class PowerExponentParser implements Parser<Generic> {

    public static final Parser<Generic> parser = new PowerExponentParser();

    private PowerExponentParser() {
    }

    public Generic parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException {
        int pos0 = p.getPosition().intValue();

        try {
            PowerParser.parser.parse(p, previousSumElement);
        } catch (ParseException e) {
            p.getPosition().setValue(pos0);
            throw e;
        }

        Generic result;
        try {
            result = ExponentParser.parser.parse(p, previousSumElement);
        } catch (ParseException e) {
            p.getPosition().setValue(pos0);
            throw e;
        }

        return result;
    }
}
