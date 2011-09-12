package jscl.text;

public class IntegerParser extends Parser {
    public static final Parser parser=new IntegerParser();

    private IntegerParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
//      StringBuffer buffer=new StringBuffer();
        int n;
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && Character.isDigit(str.charAt(pos[0]))) {
            char c=str.charAt(pos[0]++);
//          buffer.append(c);
            n=c-'0';
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        while(pos[0]<str.length() && Character.isDigit(str.charAt(pos[0]))) {
            char c=str.charAt(pos[0]++);
//          buffer.append(c);
            n=10*n+(c-'0');
        }
//      return new Integer(buffer.toString());
        return new Integer(n);
    }
}
