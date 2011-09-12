package jscl.text;

import jscl.math.NumericWrapper;
import jscl.math.numeric.JSCLDouble;

public class DoubleParser extends Parser {
    public static final Parser parser=new DoubleParser();

    private DoubleParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        double d;
        try {
            d=((Double)Singularity.parser.parse(str,pos)).doubleValue();
        } catch (ParseException e) {
            try {
                d=((Double)FloatingPointLiteral.parser.parse(str,pos)).doubleValue();
            } catch (ParseException e2) {
                throw e2;
            }
        }
        return new NumericWrapper(JSCLDouble.valueOf(d));
    }
}

class Singularity extends Parser {
    public static final Parser parser=new Singularity();

    private Singularity() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        double d;
        try {
            String s=(String)Identifier.parser.parse(str,pos);
            if(s.compareTo("NaN")==0) d=Double.NaN;
            else if(s.compareTo("Infinity")==0) d=Double.POSITIVE_INFINITY;
            else {
                pos[0]=pos0;
                throw new ParseException();
            }
        } catch (ParseException e) {
            throw e;
        }
        return new Double(d);
    }
}

class FloatingPointLiteral extends Parser {
    public static final Parser parser=new FloatingPointLiteral();

    private FloatingPointLiteral() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        StringBuffer buffer=new StringBuffer();
        boolean digits=false;
        boolean point=false;
        try {
            String s=(String)Digits.parser.parse(str,pos);
            buffer.append(s);
            digits=true;
        } catch (ParseException e) {}
        try {
            DecimalPoint.parser.parse(str,pos);
            buffer.append(".");
            point=true;
        } catch (ParseException e) {
            if(!digits) {
                pos[0]=pos0;
                throw e;
            }
        }
        try {
            String s=(String)Digits.parser.parse(str,pos);
            buffer.append(s);
        } catch (ParseException e) {
            if(!digits) {
                pos[0]=pos0;
                throw e;
            }
        }
        try {
            String s=(String)ExponentPart.parser.parse(str,pos);
            buffer.append(s);
        } catch (ParseException e) {
            if(!point) {
                pos[0]=pos0;
                throw e;
            }
        }
        return new Double(buffer.toString());
    }
}

class DecimalPoint extends Parser {
    public static final Parser parser=new DecimalPoint();

    private DecimalPoint() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='.') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        return null;
    }
}

class ExponentPart extends Parser {
    public static final Parser parser=new ExponentPart();

    private ExponentPart() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        StringBuffer buffer=new StringBuffer();
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && (str.charAt(pos[0])=='e' || str.charAt(pos[0])=='E')) {
            char c=str.charAt(pos[0]++);
            buffer.append(c);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            String s=(String)SignedInteger.parser.parse(str,pos);
            buffer.append(s);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return buffer.toString();
    }
}

class SignedInteger extends Parser {
    public static final Parser parser=new SignedInteger();

    private SignedInteger() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        StringBuffer buffer=new StringBuffer();
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && (str.charAt(pos[0])=='+' || str.charAt(pos[0])=='-')) {
            char c=str.charAt(pos[0]++);
            buffer.append(c);
        }
        try {
            int n=((Integer)IntegerParser.parser.parse(str,pos)).intValue();
            buffer.append(n);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return buffer.toString();
    }
}
