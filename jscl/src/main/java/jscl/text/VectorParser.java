package jscl.text;

import jscl.math.Generic;
import jscl.math.JsclVector;
import jscl.util.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class VectorParser implements Parser<JsclVector> {

    public static final Parser<JsclVector> parser = new VectorParser();

    private VectorParser() {
    }

    public JsclVector parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        ParserUtils.skipWhitespaces(p);

        ParserUtils.tryToParse(p, pos0, '[');

        final List<Generic> result = new ArrayList<Generic>();
        try {
            result.add(ExpressionParser.parser.parse(p, previousSumElement));
        } catch (ParseException e) {
            p.position.setValue(pos0);
            throw e;
        }

        while (true) {
            try {
                result.add(CommaAndExpression.parser.parse(p, previousSumElement));
            } catch (ParseException e) {
                p.exceptionsPool.release(e);
                break;
            }
        }

        ParserUtils.skipWhitespaces(p);

        ParserUtils.tryToParse(p, pos0, ']');

        return new JsclVector(ArrayUtils.toArray(result, new Generic[result.size()]));
    }
}
