package jscl.text;

import jscl.math.Variable;

public class VariableParser extends Parser {
    public static final Parser parser=new VariableParser();

    private VariableParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Variable v;
        try {
            v=(Variable)OperatorParser.parser.parse(str,pos);
        } catch (ParseException e) {
            try {
                v=(Variable)FunctionParser.parser.parse(str,pos);
            } catch (ParseException e2) {
                try {
                    v=(Variable)ConstantParser.parser.parse(str,pos);
                } catch (ParseException e3) {
                    throw e3;
                }
            }
        }
        return v;
    }
}
