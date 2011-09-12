package jscl.text;

public class CompoundIdentifier extends Parser {
    public static final Parser parser=new CompoundIdentifier();

    private CompoundIdentifier() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        StringBuffer buffer=new StringBuffer();
        skipWhitespaces(str,pos);
        try {
            String s=(String)Identifier.parser.parse(str,pos);
            buffer.append(s);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        while(true) {
            try {
                String s=(String)DotAndIdentifier.parser.parse(str,pos);
                buffer.append(".").append(s);
            } catch (ParseException e) {
                break;
            }
        }
        return buffer.toString();
    }
}

class DotAndIdentifier extends Parser {
    public static final Parser parser=new DotAndIdentifier();

    private DotAndIdentifier() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        String s;
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='.') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            s=(String)Identifier.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return s;
    }
}
