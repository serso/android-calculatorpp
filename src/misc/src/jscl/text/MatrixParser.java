package jscl.text;

import java.util.ArrayList;
import java.util.List;
import jscl.math.JSCLVector;
import jscl.math.Matrix;
import jscl.util.ArrayUtils;

public class MatrixParser extends Parser {
    public static final Parser parser=new MatrixParser();

    private MatrixParser() {}

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
            JSCLVector v=(JSCLVector)VectorParser.parser.parse(str,pos);
            l.add(v);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        while(true) {
            try {
                JSCLVector v=(JSCLVector)CommaAndVector.parser.parse(str,pos);
                l.add(v);
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
        JSCLVector v[]=(JSCLVector[])ArrayUtils.toArray(l,new JSCLVector[l.size()]);
        return Matrix.frame(v).transpose();
    }
}
