package jscl.text;

import jscl.math.DoubleVariable;
import jscl.math.NumericWrapper;

public class DoubleVariableParser extends Parser {
    public static final Parser parser=new DoubleVariableParser();

    private DoubleVariableParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        NumericWrapper a;
        try {
            a=(NumericWrapper)DoubleParser.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        return new DoubleVariable(a);
    }
}
