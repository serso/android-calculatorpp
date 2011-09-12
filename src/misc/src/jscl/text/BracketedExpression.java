package jscl.text;

import jscl.math.ExpressionVariable;
import jscl.math.Generic;

public class BracketedExpression extends Parser {
    public static final Parser parser=new BracketedExpression();

    private BracketedExpression() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        Generic a;
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='(') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            a=(Generic)ExpressionParser.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])==')') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        return new ExpressionVariable(a);
    }
}
