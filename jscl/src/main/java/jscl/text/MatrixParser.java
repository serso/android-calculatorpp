package jscl.text;

import jscl.math.Generic;
import jscl.math.JsclVector;
import jscl.math.Matrix;
import jscl.util.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MatrixParser implements Parser<Matrix> {

    public static final Parser<Matrix> parser = new MatrixParser();

    private MatrixParser() {
    }

    public Matrix parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        final List<Generic> vectors = new ArrayList<Generic>();

        ParserUtils.tryToParse(p, pos0, '[');

        try {
            vectors.add(VectorParser.parser.parse(p, previousSumElement));
        } catch (ParseException e) {
            p.position.setValue(pos0);
            throw e;
        }

        while (true) {
            try {
                vectors.add(CommaAndVector.parser.parse(p, previousSumElement));
            } catch (ParseException e) {
                p.exceptionsPool.release(e);
                break;
            }
        }

        ParserUtils.tryToParse(p, pos0, ']');

        return Matrix.frame((JsclVector[]) ArrayUtils.toArray(vectors, new JsclVector[vectors.size()])).transpose();
    }
}
