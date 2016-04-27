package jscl.text;

import jscl.math.Generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PostfixFunctionParser implements Parser<String> {

    @Nonnull
    private final String name;

    protected PostfixFunctionParser(@Nonnull String name) {
        this.name = name;
    }

    @Nullable
    public String parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        final int pos0 = p.position.intValue();

        ParserUtils.skipWhitespaces(p);

        if (p.position.intValue() < p.expression.length() && p.expression.startsWith(name, p.position.intValue())) {
            p.position.add(name.length());
            return name;
        } else {
            p.position.setValue(pos0);
            return null;
        }
    }
}
