package jscl.math;

import jscl.math.function.Constant;
import jscl.math.numeric.JSCLDouble;
import jscl.math.numeric.Numeric;
import jscl.math.numeric.NumericMatrix;
import jscl.math.numeric.NumericVector;
import jscl.mathml.MathML;

public final class NumericWrapper extends Generic {
    final Numeric content;

    public NumericWrapper(JSCLInteger integer) {
        content=JSCLDouble.valueOf(integer.content().doubleValue());
    }

    public NumericWrapper(Rational rational) {
        content=JSCLDouble.valueOf(rational.numerator().doubleValue()/rational.denominator().doubleValue());
    }

    public NumericWrapper(JSCLVector vector) {
        Numeric v[]=new Numeric[vector.n];
        for(int i=0;i<vector.n;i++) v[i]=((NumericWrapper)vector.element[i].numeric()).content();
        content=new NumericVector(v);
    }

    public NumericWrapper(Matrix matrix) {
        Numeric m[][]=new Numeric[matrix.n][matrix.p];
        for(int i=0;i<matrix.n;i++) {
            for(int j=0;j<matrix.p;j++) {
                m[i][j]=((NumericWrapper)matrix.element[i][j].numeric()).content();
            }
        }
        content=new NumericMatrix(m);
    }

    public NumericWrapper(Constant constant) {
        if(constant.compareTo(new Constant("pi"))==0) content=JSCLDouble.valueOf(Math.PI);
        else if(constant.compareTo(new Constant("infin"))==0) content=JSCLDouble.valueOf(Double.POSITIVE_INFINITY);
        else throw new ArithmeticException();
    }

    public NumericWrapper(Numeric numeric) {
        content=numeric;
    }

    public Numeric content() {
        return content;
    }

    public NumericWrapper add(NumericWrapper wrapper) {
        return new NumericWrapper(content.add(wrapper.content));
    }

    public Generic add(Generic generic) {
        if(generic instanceof NumericWrapper) {
            return add((NumericWrapper)generic);
        } else {
            return add(valueof(generic));
        }
    }

    public NumericWrapper subtract(NumericWrapper wrapper) {
        return new NumericWrapper(content.subtract(wrapper.content));
    }

    public Generic subtract(Generic generic) {
        if(generic instanceof NumericWrapper) {
            return subtract((NumericWrapper)generic);
        } else {
            return subtract(valueof(generic));
        }
    }

    public NumericWrapper multiply(NumericWrapper wrapper) {
        return new NumericWrapper(content.multiply(wrapper.content));
    }

    public Generic multiply(Generic generic) {
        if(generic instanceof NumericWrapper) {
            return multiply((NumericWrapper)generic);
        } else {
            return multiply(valueof(generic));
        }
    }

    public NumericWrapper divide(NumericWrapper wrapper) throws ArithmeticException {
        return new NumericWrapper(content.divide(wrapper.content));
    }

    public Generic divide(Generic generic) throws ArithmeticException {
        if(generic instanceof NumericWrapper) {
            return divide((NumericWrapper)generic);
        } else {
            return divide(valueof(generic));
        }
    }

    public Generic gcd(Generic generic) {
        return null;
    }

    public Generic gcd() {
        return null;
    }

    public Generic abs() {
        return new NumericWrapper(content.abs());
    }

    public Generic negate() {
        return new NumericWrapper(content.negate());
    }

    public int signum() {
        return content.signum();
    }

    public int degree() {
        return 0;
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        return null;
    }

    public Generic derivative(Variable variable) {
        return null;
    }

    public Generic substitute(Variable variable, Generic generic) {
        return null;
    }

    public Generic expand() {
        return null;
    }

    public Generic factorize() {
        return null;
    }

    public Generic elementary() {
        return null;
    }

    public Generic simplify() {
        return null;
    }

    public Generic numeric() {
        return this;
    }

    public NumericWrapper valueof(NumericWrapper wrapper) {
        return new NumericWrapper(content.valueof(wrapper.content));
    }

    public Generic valueof(Generic generic) {
        if(generic instanceof NumericWrapper) {
            return valueof((NumericWrapper)generic);
        } else if(generic instanceof JSCLInteger) {
            return new NumericWrapper((JSCLInteger)generic);
        } else throw new ArithmeticException();
    }

    public Generic[] sumValue() {
        return null;
    }

    public Generic[] productValue() throws NotProductException {
        return null;
    }

    public Power powerValue() throws NotPowerException {
        return null;
    }

    public Expression expressionValue() throws NotExpressionException {
        throw new NotExpressionException();
    }

    public JSCLInteger integerValue() throws NotIntegerException {
        if(content instanceof JSCLDouble) return JSCLInteger.valueOf((int)((JSCLDouble)content).doubleValue());
        else throw new NotIntegerException();
    }

    public Variable variableValue() throws NotVariableException {
        throw new NotVariableException();
    }

    public Variable[] variables() {
        return new Variable[0];
    }

    public boolean isPolynomial(Variable variable) {
        return true;
    }

    public boolean isConstant(Variable variable) {
        return true;
    }

    public Generic sgn() {
        return new NumericWrapper(content.sgn());
    }

    public Generic log() {
        return new NumericWrapper(content.log());
    }

    public Generic exp() {
        return new NumericWrapper(content.exp());
    }

    public Generic pow(Generic generic) {
        return new NumericWrapper(content.pow(((NumericWrapper)generic).content));
    }

    public Generic sqrt() {
        return new NumericWrapper(content.sqrt());
    }

    public Generic nthrt(int n) {
        return new NumericWrapper(content.nthrt(n));
    }

    public static Generic root(int subscript, Generic parameter[]) {
        Numeric param[]=new Numeric[parameter.length];
        for(int i=0;i<param.length;i++) param[i]=((NumericWrapper)parameter[i]).content;
        return new NumericWrapper(Numeric.root(subscript,param));
    }

    public Generic conjugate() {
        return new NumericWrapper(content.conjugate());
    }

    public Generic acos() {
        return new NumericWrapper(content.acos());
    }

    public Generic asin() {
        return new NumericWrapper(content.asin());
    }

    public Generic atan() {
        return new NumericWrapper(content.atan());
    }

    public Generic acot() {
        return new NumericWrapper(content.acot());
    }

    public Generic cos() {
        return new NumericWrapper(content.cos());
    }

    public Generic sin() {
        return new NumericWrapper(content.sin());
    }

    public Generic tan() {
        return new NumericWrapper(content.tan());
    }

    public Generic cot() {
        return new NumericWrapper(content.cot());
    }

    public Generic acosh() {
        return new NumericWrapper(content.acosh());
    }

    public Generic asinh() {
        return new NumericWrapper(content.asinh());
    }

    public Generic atanh() {
        return new NumericWrapper(content.atanh());
    }

    public Generic acoth() {
        return new NumericWrapper(content.acoth());
    }

    public Generic cosh() {
        return new NumericWrapper(content.cosh());
    }

    public Generic sinh() {
        return new NumericWrapper(content.sinh());
    }

    public Generic tanh() {
        return new NumericWrapper(content.tanh());
    }

    public Generic coth() {
        return new NumericWrapper(content.coth());
    }

    public int compareTo(NumericWrapper wrapper) {
        return content.compareTo(wrapper.content);
    }

    public int compareTo(Generic generic) {
        if(generic instanceof NumericWrapper) {
            return compareTo((NumericWrapper)generic);
        } else {
            return compareTo(valueof(generic));
        }
    }

    public String toString() {
        return content.toString();
    }

    public String toJava() {
        return "JSCLDouble.valueOf("+new Double(((JSCLDouble)content).doubleValue())+")";
    }

    public void toMathML(MathML element, Object data) {
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) bodyToMathML(element);
        else {
            MathML e1=element.element("msup");
            bodyToMathML(e1);
            MathML e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    void bodyToMathML(MathML element) {
        MathML e1=element.element("mn");
        e1.appendChild(element.text(String.valueOf(new Double(((JSCLDouble)content).doubleValue()))));
        element.appendChild(e1);
    }

    protected Generic newinstance() {
        return null;
    }
}
