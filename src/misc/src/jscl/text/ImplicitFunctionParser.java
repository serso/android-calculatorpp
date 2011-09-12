package jscl.text;

import java.util.ArrayList;
import java.util.List;
import jscl.math.Generic;
import jscl.math.function.ImplicitFunction;
import jscl.util.ArrayUtils;

public class ImplicitFunctionParser extends Parser {
    public static final Parser parser=new ImplicitFunctionParser();

    private ImplicitFunctionParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        String name;
        Generic a[];
        int b[];
        List l=new ArrayList();
        try {
            name=(String)CompoundIdentifier.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
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
            b=(int [])Derivation.parser.parse(str,pos);
        } catch (ParseException e) {
            b=new int[0];
                }
        try {
            a=(Generic[])ParameterList.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        Generic s[]=(Generic[])ArrayUtils.toArray(l,new Generic[l.size()]);
        int derivation[]=new int[a.length];
        for(int i=0;i<a.length && i<b.length;i++) derivation[i]=b[i];
        return new ImplicitFunction(name,a,derivation,s);
    }
}

class Derivation extends Parser {
    public static final Parser parser=new Derivation();

    private Derivation() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        int c[];
        try {
            c=new int[] {((Integer)PrimeCharacters.parser.parse(str,pos)).intValue()};
        } catch (ParseException e) {
            try {
                c=(int [])SuperscriptList.parser.parse(str,pos);
            } catch (ParseException e2) {
                throw e2;
            }
        }
        return c;
    }
}

class SuperscriptList extends Parser {
    public static final Parser parser=new SuperscriptList();

    private SuperscriptList() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        List l=new ArrayList();
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='{') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            Integer in=(Integer)IntegerParser.parser.parse(str,pos);
            l.add(in);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        while(true) {
            try {
                Integer in=(Integer)CommaAndInteger.parser.parse(str,pos);
                l.add(in);
            } catch (ParseException e) {
                break;
            }
        }
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='}') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        return (int[])ArrayUtils.toArray(l,new int[l.size()]);
    }
}

class CommaAndInteger extends Parser {
    public static final Parser parser=new CommaAndInteger();

    private CommaAndInteger() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        int c;
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])==',') {
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
        return new Integer(c);
    }
}
