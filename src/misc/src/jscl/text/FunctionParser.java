package jscl.text;

import jscl.math.Generic;
import jscl.math.function.Abs;
import jscl.math.function.Comparison;
import jscl.math.function.Conjugate;
import jscl.math.function.Cubic;
import jscl.math.function.Exp;
import jscl.math.function.Function;
import jscl.math.function.Log;
import jscl.math.function.Sgn;
import jscl.math.function.Sqrt;
import jscl.math.function.hyperbolic.Acosh;
import jscl.math.function.hyperbolic.Acoth;
import jscl.math.function.hyperbolic.Asinh;
import jscl.math.function.hyperbolic.Atanh;
import jscl.math.function.hyperbolic.Cosh;
import jscl.math.function.hyperbolic.Coth;
import jscl.math.function.hyperbolic.Sinh;
import jscl.math.function.hyperbolic.Tanh;
import jscl.math.function.trigonometric.Acos;
import jscl.math.function.trigonometric.Acot;
import jscl.math.function.trigonometric.Asin;
import jscl.math.function.trigonometric.Atan;
import jscl.math.function.trigonometric.Cos;
import jscl.math.function.trigonometric.Cot;
import jscl.math.function.trigonometric.Sin;
import jscl.math.function.trigonometric.Tan;

public class FunctionParser extends Parser {
    public static final Parser parser=new FunctionParser();

    private FunctionParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        Function v;
        try {
            v=(Function)UsualFunctionParser.parser.parse(str,pos);
        } catch (ParseException e) {
            try {
                v=(Function)RootParser.parser.parse(str,pos);
            } catch (ParseException e2) {
                try {
                    v=(Function)ImplicitFunctionParser.parser.parse(str,pos);
                } catch (ParseException e3) {
                    throw e3;
                }
            }
        }
        return v;
    }
}

class UsualFunctionParser extends Parser {
    public static final Parser parser=new UsualFunctionParser();

    private UsualFunctionParser() {}

    public Object parse(String str, int pos[]) throws ParseException {
        int pos0=pos[0];
        String name;
        Generic a[];
        try {
            name=(String)Identifier.parser.parse(str,pos);
            if(valid(name));
            else {
                pos[0]=pos0;
                throw new ParseException();
            }
        } catch (ParseException e) {
            throw e;
        }
        try {
            a=(Generic[])ParameterList.parser.parse(str,pos);
        } catch (ParseException e) {
            pos[0]=pos0;
            throw e;
        }
        Function v=null;
        if(name.compareTo("sin")==0) v=new Sin(a[0]);
        else if(name.compareTo("cos")==0) v=new Cos(a[0]);
        else if(name.compareTo("tan")==0) v=new Tan(a[0]);
        else if(name.compareTo("cot")==0) v=new Cot(a[0]);
        else if(name.compareTo("asin")==0) v=new Asin(a[0]);
        else if(name.compareTo("acos")==0) v=new Acos(a[0]);
        else if(name.compareTo("atan")==0) v=new Atan(a[0]);
        else if(name.compareTo("acot")==0) v=new Acot(a[0]);
        else if(name.compareTo("log")==0) v=new Log(a[0]);
        else if(name.compareTo("exp")==0) v=new Exp(a[0]);
        else if(name.compareTo("sqrt")==0) v=new Sqrt(a[0]);
        else if(name.compareTo("cubic")==0) v=new Cubic(a[0]);
        else if(name.compareTo("sinh")==0) v=new Sinh(a[0]);
        else if(name.compareTo("cosh")==0) v=new Cosh(a[0]);
        else if(name.compareTo("tanh")==0) v=new Tanh(a[0]);
        else if(name.compareTo("coth")==0) v=new Coth(a[0]);
        else if(name.compareTo("asinh")==0) v=new Asinh(a[0]);
        else if(name.compareTo("acosh")==0) v=new Acosh(a[0]);
        else if(name.compareTo("atanh")==0) v=new Atanh(a[0]);
        else if(name.compareTo("acoth")==0) v=new Acoth(a[0]);
        else if(name.compareTo("abs")==0) v=new Abs(a[0]);
        else if(name.compareTo("sgn")==0) v=new Sgn(a[0]);
        else if(name.compareTo("conjugate")==0) v=new Conjugate(a[0]);
        else if(name.compareTo("eq")==0 || name.compareTo("le")==0 || name.compareTo("ge")==0 || name.compareTo("ne")==0 || name.compareTo("lt")==0 || name.compareTo("gt")==0 || name.compareTo("ap")==0) v=new Comparison(name,a[0],a[1]);
        return v;
    }

    static boolean valid(String name) {
        for(int i=0;i<na.length;i++) if(name.compareTo(na[i])==0) return true;
        return false;
    }

    private static String na[]={"sin","cos","tan","cot","asin","acos","atan","acot","log","exp","sqrt","cubic","sinh","cosh","tanh","coth","asinh","acosh","atanh","acoth","abs","sgn","conjugate","eq","le","ge","ne","lt","gt","ap"};
}
