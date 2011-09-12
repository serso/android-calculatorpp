package jscl.text;

import java.math.BigInteger;
import jscl.math.JSCLInteger;

public class JSCLIntegerParser extends Parser {
    public static final Parser parser=new JSCLIntegerParser();

    private JSCLIntegerParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        StringBuffer buffer=new StringBuffer();
        try {
            String s=(String)Digits.parser.parse(str,pos);
            buffer.append(s);
        } catch (ParseException e) {
            throw e;
        }
        return new JSCLInteger(new BigInteger(buffer.toString()));
    }
}
