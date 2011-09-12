package jscl.text;

import jscl.math.JSCLVector;
import jscl.math.VectorVariable;

public class VectorVariableParser extends Parser {
    public static final Parser parser=new VectorVariableParser();

    private VectorVariableParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        JSCLVector v;
        try {
            v=(JSCLVector)VectorParser.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        return new VectorVariable(v);
    }
}
