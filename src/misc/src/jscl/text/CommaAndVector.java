package jscl.text;

import jscl.math.JSCLVector;

public class CommaAndVector extends Parser {
    public static final Parser parser=new CommaAndVector();

    private CommaAndVector() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        JSCLVector v;
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])==',') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            v=(JSCLVector)VectorParser.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return v;
    }
}
