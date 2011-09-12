package jscl.text;

public class Identifier extends Parser {
    public static final Parser parser=new Identifier();

    private Identifier() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        StringBuffer buffer=new StringBuffer();
        skipWhitespaces(str,pos);
//      if(pos[0]<str.length() && Character.isLetter(str.charAt(pos[0]))) {
        if(pos[0]<str.length() && isLetter(str.charAt(pos[0]))) {
            char c=str.charAt(pos[0]++);
            buffer.append(c);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
//      while(pos[0]<str.length() && Character.isLetterOrDigit(str.charAt(pos[0]))) {
        while(pos[0]<str.length() && (isLetter(str.charAt(pos[0])) || Character.isDigit(str.charAt(pos[0])))) {
            char c=str.charAt(pos[0]++);
            buffer.append(c);
        }
        return buffer.toString();
    }

    static boolean isLetter(char c) {
        return (c>='A' && c<='Z') || (c>='a' && c<='z') || c=='_';
    }
}
