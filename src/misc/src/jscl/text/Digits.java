package jscl.text;

public class Digits extends Parser {
    public static final Parser parser=new Digits();

    private Digits() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        StringBuffer buffer=new StringBuffer();
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && Character.isDigit(str.charAt(pos[0]))) {
            char c=str.charAt(pos[0]++);
            buffer.append(c);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        while(pos[0]<str.length() && Character.isDigit(str.charAt(pos[0]))) {
            char c=str.charAt(pos[0]++);
            buffer.append(c);
        }
        return buffer.toString();
    }
}
