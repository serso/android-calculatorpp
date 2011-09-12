package jscl.math.numeric;

public final class Complex extends Numeric {
    double real, imag;

    Complex(double real, double imag) {
        this.real=real;
        this.imag=imag;
    }

    public Complex add(Complex complex) {
        return new Complex(real+complex.real,imag+complex.imag);
    }

    public Numeric add(Numeric numeric) {
        if(numeric instanceof Complex) {
            return add((Complex)numeric);
        } else if(numeric instanceof JSCLDouble) {
            return add(valueof(numeric));
        } else {
            return numeric.valueof(this).add(numeric);
        }
    }

    public Complex subtract(Complex complex) {
        return new Complex(real-complex.real,imag-complex.imag);
    }

    public Numeric subtract(Numeric numeric) {
        if(numeric instanceof Complex) {
            return subtract((Complex)numeric);
        } else if(numeric instanceof JSCLDouble) {
            return subtract(valueof(numeric));
        } else {
            return numeric.valueof(this).subtract(numeric);
        }
    }

    public Complex multiply(Complex complex) {
        return new Complex(real*complex.real-imag*complex.imag,real*complex.imag+imag*complex.real);
    }

    public Numeric multiply(Numeric numeric) {
        if(numeric instanceof Complex) {
            return multiply((Complex)numeric);
        } else if(numeric instanceof JSCLDouble) {
            return multiply(valueof(numeric));
        } else {
            return numeric.multiply(this);
        }
    }

    public Complex divide(Complex complex) throws ArithmeticException {
        return multiply((Complex)complex.inverse());
    }

    public Numeric divide(Numeric numeric) throws ArithmeticException {
        if(numeric instanceof Complex) {
            return divide((Complex)numeric);
        } else if(numeric instanceof JSCLDouble) {
            return divide(valueof(numeric));
        } else {
            return numeric.valueof(this).divide(numeric);
        }
    }

    public Numeric negate() {
        return new Complex(-real,-imag);
    }

    public int signum() {
        return imag==0.?(real==0.?0:(real<0.?-1:1)):(imag<0.?-1:1);
    }

    public Complex valueof(Complex complex) {
        return new Complex(complex.real,complex.imag);
    }

    public Numeric valueof(Numeric numeric) {
        if(numeric instanceof Complex) {
            return valueof((Complex)numeric);
        } else if(numeric instanceof JSCLDouble) {
            JSCLDouble d=(JSCLDouble)numeric;
            return new Complex(d.content,0.);
        } else throw new ArithmeticException();
    }

    public double magnitude() {
        return Math.sqrt(real*real+imag*imag);
    }

    public double magnitude2() {
        return real*real+imag*imag;
    }

    public double angle() {
        return Math.atan2(imag,real);
    }

    public Numeric log() {
        if(signum()==0) {
            return JSCLDouble.valueOf(0).log();
        } else {
            return new Complex(Math.log(magnitude()),angle());
        }
    }

    public Numeric exp() {
        return new Complex(Math.cos(imag),Math.sin(imag)).multiply(Math.exp(real));
    }

    public Numeric inverse() {
        return ((Complex)conjugate()).divide(magnitude2());
    }

    Complex multiply(double d) {
        return new Complex(real*d,imag*d);
    }

    Complex divide(double d) {
        return new Complex(real/d,imag/d);
    }

    public Numeric conjugate() {
        return new Complex(real,-imag);
    }

    public double realPart() {
        return real;
    }

    public double imaginaryPart() {
        return imag;
    }

    public int compareTo(Complex complex) {
        if(imag<complex.imag) return -1;
        else if(imag>complex.imag) return 1;
        else if(imag==complex.imag) {
            if(real<complex.real) return -1;
            else if(real>complex.real) return 1;
            else if(real==complex.real) return 0;
            else throw new ArithmeticException();
        } else throw new ArithmeticException();
    }

    public int compareTo(Numeric numeric) {
        if(numeric instanceof Complex) {
            return compareTo((Complex)numeric);
        } else if(numeric instanceof JSCLDouble) {
            return compareTo(valueof(numeric));
        } else {
            return numeric.valueof(this).compareTo(numeric);
        }
    }

    public static Complex valueOf(double real, double imag) {
        return new Complex(real,imag);
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        if(imag==0.) {
            buffer.append(real);
        } else {
            if(real==0.);
            else {
                buffer.append(real);
                if(imag<=0.);
                                else buffer.append("+");
            }
            if(imag==1.);
            else if(imag==-1.) buffer.append("-");
            else {
                buffer.append(imag);
                buffer.append("*");
            }
            buffer.append("sqrt(-1)");
        }
        return buffer.toString();
    }
}
