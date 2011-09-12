package jscl.text;

import java.util.ArrayList;
import java.util.List;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.util.ArrayUtils;

public class ConstantParser extends Parser {
    public static final Parser parser=new ConstantParser();

    private ConstantParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        String name;
        int prime=0;
        List l=new ArrayList();
        try {
            name=(String)CompoundIdentifier.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        while(true) {
            try {
                Generic s=(Generic)Subscript.parser.parse(str,pos);
                l.add(s);
            } catch (ParseException e) {
                break;
            }
        }
        try {
            prime=((Integer)Prime.parser.parse(str,pos)).intValue();
        } catch (ParseException e) {}
        Generic s[]=(Generic[])ArrayUtils.toArray(l,new Generic[l.size()]);
        Constant v=new Constant(name,prime,s);
        return v;
    }
}

class Prime extends Parser {
    public static final Parser parser=new Prime();

    private Prime() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        int c;
        try {
            c=((Integer)PrimeCharacters.parser.parse(str,pos)).intValue();
        } catch (ParseException e) {
            try {
                c=((Integer)Superscript.parser.parse(str,pos)).intValue();
            } catch (ParseException e2) {
                throw e2;
            }
        }
        return new Integer(c);
    }
}

class Superscript extends Parser {
    public static final Parser parser=new Superscript();

    private Superscript() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        int c;
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='{') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            c=((Integer)IntegerParser.parser.parse(str,pos)).intValue();
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='}') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        return new Integer(c);
    }
}
