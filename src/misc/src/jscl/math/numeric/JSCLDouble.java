package jscl.math.numeric;

public final class JSCLDouble extends Numeric {
    double content;

    JSCLDouble(double val) {
        content=val;
    }

    public JSCLDouble add(JSCLDouble dble) {
        return new JSCLDouble(content+dble.content);
    }

    public Numeric add(Numeric numeric) {
        if(numeric instanceof JSCLDouble) {
            return add((JSCLDouble)numeric);
        } else {
            return numeric.valueof(this).add(numeric);
        }
    }

    public JSCLDouble subtract(JSCLDouble dble) {
        return new JSCLDouble(content-dble.content);
    }

    public Numeric subtract(Numeric numeric) {
        if(numeric instanceof JSCLDouble) {
            return subtract((JSCLDouble)numeric);
        } else {
            return numeric.valueof(this).subtract(numeric);
        }
    }

    public JSCLDouble multiply(JSCLDouble dble) {
        return new JSCLDouble(content*dble.content);
    }

    public Numeric multiply(Numeric numeric) {
        if(numeric instanceof JSCLDouble) {
            return multiply((JSCLDouble)numeric);
        } else {
            return numeric.multiply(this);
        }
    }

    public JSCLDouble divide(JSCLDouble dble) throws ArithmeticException {
        return new JSCLDouble(content/dble.content);
    }

    public Numeric divide(Numeric numeric) throws ArithmeticException {
        if(numeric instanceof JSCLDouble) {
            return divide((JSCLDouble)numeric);
        } else {
            return numeric.valueof(this).divide(numeric);
        }
    }

    public Numeric negate() {
        return new JSCLDouble(-content);
    }

    public int signum() {
        return content==0.?0:(content<0.?-1:1);
    }

    public Numeric log() {
        return new JSCLDouble(Math.log(content));
    }

    public Numeric exp() {
        return new JSCLDouble(Math.exp(content));
    }

    public Numeric inverse() {
        return new JSCLDouble(1./content);
    }

    public Numeric pow(JSCLDouble dble) {
        if(signum()<0) {
            return Complex.valueOf(content,0).pow(dble);
        } else {
            return new JSCLDouble(Math.pow(content,dble.content));
        }
    }

    public Numeric pow(Numeric numeric) {
        if(numeric instanceof JSCLDouble) {
            return pow((JSCLDouble)numeric);
        } else {
            return numeric.valueof(this).pow(numeric);
        }
    }

    public Numeric sqrt() {
        if(signum()<0) {
            return Complex.valueOf(0,1).multiply(negate().sqrt());
        } else {
            return new JSCLDouble(Math.sqrt(content));
        }
    }

    public Numeric nthrt(int n) {
        if(signum()<0) {
            return n%2==0?sqrt().nthrt(n/2):negate().nthrt(n).negate();
        } else {
            return super.nthrt(n);
        }
    }

    public Numeric conjugate() {
        return this;
    }

    public Numeric acos() {
        return new JSCLDouble(Math.acos(content));
    }

    public Numeric asin() {
        return new JSCLDouble(Math.asin(content));
    }

    public Numeric atan() {
        return new JSCLDouble(Math.atan(content));
    }

    public Numeric cos() {
        return new JSCLDouble(Math.cos(content));
    }

    public Numeric sin() {
        return new JSCLDouble(Math.sin(content));
    }

    public Numeric tan() {
        return new JSCLDouble(Math.tan(content));
    }

    public JSCLDouble valueof(JSCLDouble dble) {
        return new JSCLDouble(dble.content);
    }

    public Numeric valueof(Numeric numeric) {
        if(numeric instanceof JSCLDouble) {
            return valueof((JSCLDouble)numeric);
        } else throw new ArithmeticException();
    }

    public double doubleValue() {
        return content;
    }

    public int compareTo(JSCLDouble dble) {
        if(content<dble.content) return -1;
        else if(content>dble.content) return 1;
        else if(content==dble.content) return 0;
        else throw new ArithmeticException();
    }

    public int compareTo(Numeric numeric) {
        if(numeric instanceof JSCLDouble) {
            return compareTo((JSCLDouble)numeric);
        } else {
            return numeric.valueof(this).compareTo(numeric);
        }
    }

    public static JSCLDouble valueOf(double val) {
        return new JSCLDouble(val);
    }

    public String toString() {
        return new Double(content).toString();
    }
}
