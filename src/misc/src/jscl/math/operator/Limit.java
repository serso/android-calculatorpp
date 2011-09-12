package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;
import jscl.mathml.MathML;

public class Limit extends Operator {
    public Limit(Generic expression, Generic variable, Generic limit, Generic direction) {
        super("lim",new Generic[] {expression,variable,limit,direction});
    }

    public Generic compute() {
        return expressionValue();
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        int n=4;
        if(parameter[3].signum()==0) n=3;
        buffer.append(name);
        buffer.append("(");
        for(int i=0;i<n;i++) {
            buffer.append(parameter[i]).append(i<n-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) bodyToMathML(element);
        else {
            MathML e1=element.element("msup");
            MathML e2=element.element("mfenced");
            bodyToMathML(e2);
            e1.appendChild(e2);
            e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    void bodyToMathML(MathML element) {
        int c=parameter[3].signum();
        MathML e1=element.element("mrow");
        MathML e2=element.element("munder");
        MathML e3=element.element("mo");
        e3.appendChild(element.text("lim"));
        e2.appendChild(e3);
        e3=element.element("mrow");
        parameter[1].toMathML(e3,null);
        MathML e4=element.element("mo");
        e4.appendChild(element.text("\u2192"));
        e3.appendChild(e4);
        if(c==0) parameter[2].toMathML(e3,null);
        else {
            e4=element.element("msup");
            parameter[2].toMathML(e4,null);
            MathML e5=element.element("mo");
            if(c<0) e5.appendChild(element.text("-"));
            else if(c>0) e5.appendChild(element.text("+"));
            e4.appendChild(e5);
            e3.appendChild(e4);
        }
        e2.appendChild(e3);
        e1.appendChild(e2);
        parameter[0].toMathML(e1,null);
        element.appendChild(e1);
    }

    protected Variable newinstance() {
        return new Limit(null,null,null,null);
    }
}
