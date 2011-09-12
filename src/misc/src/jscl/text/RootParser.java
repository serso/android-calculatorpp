package jscl.text;

import jscl.math.Generic;
import jscl.math.function.Root;

public class RootParser extends Parser {
    public static final Parser parser=new RootParser();

    private RootParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        String name;
        Generic subscript;
        Generic a[];
        try {
            name=(String)Identifier.parser.parse(str,pos);
            if(name.compareTo("root")==0);
            else {
                pos[0]=pos0;
                throw new ParseException();
            }
        } catch (ParseException e) {
            throw e;
        }
        try {
            subscript=(Generic)Subscript.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        try {
            a=(Generic[])ParameterList.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return new Root(a,subscript);
    }
}
