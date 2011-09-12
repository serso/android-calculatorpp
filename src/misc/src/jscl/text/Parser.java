package jscl.text;

public abstract class Parser {
    public abstract Object parse(String str, int pos[]) throws ParseException;

    public static void skipWhitespaces(String str, int pos[]) {
//      while(pos[0]<str.length() && Character.isWhitespace(str.charAt(pos[0]))) pos[0]++;
        while(pos[0]<str.length() && isWhitespace(str.charAt(pos[0]))) pos[0]++;
    }

    static boolean isWhitespace(char c) {
        return c==' ' || c=='\t' || c=='\n';
    }
}
