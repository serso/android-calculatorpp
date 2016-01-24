package jscl.text;

import jscl.math.Generic;
import jscl.math.JsclVector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CommaAndVector implements Parser<JsclVector> {

    public static final Parser<JsclVector> parser = new CommaAndVector();

    private CommaAndVector() {
    }

    @Nonnull
    public JsclVector parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        ParserUtils.skipWhitespaces(p);

        ParserUtils.tryToParse(p, pos0, ',');

        return ParserUtils.parseWithRollback(VectorParser.parser, pos0, previousSumElement, p);
    }
}
