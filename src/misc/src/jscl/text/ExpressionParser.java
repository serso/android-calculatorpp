package jscl.text;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;
import jscl.math.function.Frac;
import jscl.math.function.Inv;
import jscl.math.function.Pow;
import jscl.math.operator.Factorial;

public class ExpressionParser extends Parser {
    public static final Parser parser=new ExpressionParser();

    private ExpressionParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Generic a;
        boolean sign=false;
        try {
            MinusParser.parser.parse(str,pos);
            sign=true;
        } catch (ParseException e) {}
        try {
            a=(Generic)TermParser.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        if(sign) a=a.negate();
        while(true) {
            try {
                Generic a2=(Generic)PlusOrMinusTerm.parser.parse(str,pos);
                a=a.add(a2);
            } catch (ParseException e) {
                break;
            }
        }
        return a;
    }
}

class MinusParser extends Parser {
    public static final Parser parser=new MinusParser();

    private MinusParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='-') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        return null;
    }
}

class PlusOrMinusTerm extends Parser {
    public static final Parser parser=new PlusOrMinusTerm();

    private PlusOrMinusTerm() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        boolean sign;
        Generic a;
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && (str.charAt(pos[0])=='+' || str.charAt(pos[0])=='-')) {
            sign=str.charAt(pos[0]++)=='-';
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            a=(Generic)TermParser.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return sign?a.negate():a;
    }
}

class TermParser extends Parser {
    public static final Parser parser=new TermParser();

    private TermParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Generic a=JSCLInteger.valueOf(1);
        Generic s;
        try {
            s=(Generic)UnsignedFactor.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        while(true) {
            try {
                Generic b=(Generic)MultiplyOrDivideFactor.multiply.parse(str,pos);
                a=a.multiply(s);
                s=b;
            } catch (ParseException e) {
                try {
                    Generic b=(Generic)MultiplyOrDivideFactor.divide.parse(str,pos);
                    if(s.compareTo(JSCLInteger.valueOf(1))==0) s=new Inv(GenericVariable.content(b,true)).expressionValue();
                    else s=new Frac(GenericVariable.content(s,true),GenericVariable.content(b,true)).expressionValue();
                } catch (ParseException e2) {
                    break;
                }
            }
        }
        a=a.multiply(s);
        return a;
    }
}

class MultiplyOrDivideFactor extends Parser {
    public static final Parser multiply=new MultiplyOrDivideFactor(true);
    public static final Parser divide=new MultiplyOrDivideFactor(false);
    boolean option;

    private MultiplyOrDivideFactor(boolean option) {
        this.option=option;
    }

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        Generic a;
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])==(option?'*':'/')) {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        try {
            a=(Generic)Factor.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return a;
    }
}

class Factor extends Parser {
    public static final Parser parser=new Factor();

    private Factor() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Generic a;
        boolean sign=false;
        try {
            MinusParser.parser.parse(str,pos);
            sign=true;
        } catch (ParseException e) {}
        try {
            a=(Generic)UnsignedFactor.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        return sign?a.negate():a;
    }
}

class UnsignedFactor extends Parser {
    public static final Parser parser=new UnsignedFactor();

    private UnsignedFactor() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Generic a;
        List l=new ArrayList();
        try {
            a=(Generic)UnsignedExponent.parser.parse(str,pos);
            l.add(a);
        } catch (ParseException e) {
            throw e;
        }
        while(true) {
            try {
                a=(Generic)PowerExponent.parser.parse(str,pos);
                l.add(a);
            } catch (ParseException e) {
                break;
            }
        }
        ListIterator it=l.listIterator(l.size());
        a=(Generic)it.previous();
        while(it.hasPrevious()) {
            Generic b=(Generic)it.previous();
            try {
                int c=a.integerValue().intValue();
                if(c<0) a=new Pow(GenericVariable.content(b,true),JSCLInteger.valueOf(c)).expressionValue();
                else a=b.pow(c);
            } catch (NotIntegerException e) {
                a=new Pow(GenericVariable.content(b,true),GenericVariable.content(a,true)).expressionValue();
            }
        }
        return a;
    }
}

class PowerExponent extends Parser {
    public static final Parser parser=new PowerExponent();

    private PowerExponent() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        Generic a;
        try {
            PowerParser.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        try {
            a=(Generic)Exponent.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return a;
    }
}

class PowerParser extends Parser {
    public static final Parser parser=new PowerParser();

    private PowerParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='^') {
            str.charAt(pos[0]++);
        } else {
            if(pos[0]+1<str.length() && str.charAt(pos[0])=='*' && str.charAt(pos[0]+1)=='*') {
                str.charAt(pos[0]++);
                str.charAt(pos[0]++);
            } else {
                pos[0]=pos0;
                throw new ParseException();
            }
        }
        return null;
    }
}

class Exponent extends Parser {
    public static final Parser parser=new Exponent();

    private Exponent() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        Generic a;
        boolean sign=false;
        try {
            MinusParser.parser.parse(str,pos);
            sign=true;
        } catch (ParseException e) {}
        try {
            a=(Generic)UnsignedExponent.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        return sign?a.negate():a;
    }
}

class UnsignedExponent extends Parser {
    public static final Parser parser=new UnsignedExponent();

    private UnsignedExponent() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        Generic a;
        boolean factorial=false;
        try {
            a=(Generic)PrimaryExpression.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        try {
            FactorialParser.parser.parse(str,pos);
            factorial=true;
        } catch (ParseException e) {}
        return factorial?new Factorial(GenericVariable.content(a,true)).expressionValue():a;
    }
}

class FactorialParser extends Parser {
    public static final Parser parser=new FactorialParser();

    private FactorialParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        skipWhitespaces(str,pos);
        if(pos[0]<str.length() && str.charAt(pos[0])=='!') {
            str.charAt(pos[0]++);
        } else {
            pos[0]=pos0;
            throw new ParseException();
        }
        return null;
    }
}

class PrimaryExpression extends Parser {
    public static final Parser parser=new PrimaryExpression();

    private PrimaryExpression() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Generic a;
        try {
            a=((Variable)DoubleVariableParser.parser.parse(str,pos)).expressionValue();
        } catch (ParseException e) {
            try {
                a=(Generic)JSCLIntegerParser.parser.parse(str,pos);
            } catch (ParseException e2) {
                try {
                    a=((Variable)VariableParser.parser.parse(str,pos)).expressionValue();
                } catch (ParseException e3) {
                    try {
                        a=((Variable)MatrixVariableParser.parser.parse(str,pos)).expressionValue();
                    } catch (ParseException e4) {
                        try {
                            a=((Variable)VectorVariableParser.parser.parse(str,pos)).expressionValue();
                        } catch (ParseException e5) {
                            try {
                                a=((Variable)BracketedExpression.parser.parse(str,pos)).expressionValue();
                            } catch (ParseException e6) {
                                throw e6;
                            }
                        }
                    }
                }
            }
        }
        return a;
    }
}
