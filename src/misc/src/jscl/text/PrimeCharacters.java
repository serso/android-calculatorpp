package jscl.text;

public class PrimeCharacters extends Parser {
    public static final Parser parser=new PrimeCharacters();

    private PrimeCharacters() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        int c;
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='\'') {
            str.charAt(pos[0]++);
            c=1;
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        while(pos[0]<str.length() && str.charAt(pos[0])=='\'') {
            str.charAt(pos[0]++);
            c++;
        }
        return new Integer(c);
    }
}
