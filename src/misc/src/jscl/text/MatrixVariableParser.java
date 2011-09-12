package jscl.text;

import jscl.math.Matrix;
import jscl.math.MatrixVariable;

class MatrixVariableParser extends Parser {
    public static final Parser parser=new MatrixVariableParser();

    private MatrixVariableParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Matrix m;
        try {
            m=(Matrix)MatrixParser.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        return new MatrixVariable(m);
    }
}
