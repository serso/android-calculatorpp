package jscl.text;

import java.util.ArrayList;
import java.util.List;
import jscl.math.Generic;
import jscl.util.ArrayUtils;

public class ParameterList extends Parser {
    public static final Parser parser=new ParameterList();

    private ParameterList() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        List l=new ArrayList();
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='(') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            Generic a=(Generic)ExpressionParser.parser.parse(str,pos);
            l.add(a);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        while(true) {
            try {
                Generic a=(Generic)CommaAndExpression.parser.parse(str,pos);
                l.add(a);
            } catch (ParseException e) {
                break;
            }
        }
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])==')') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        return (Generic[])ArrayUtils.toArray(l,new Generic[l.size()]);
    }
}
