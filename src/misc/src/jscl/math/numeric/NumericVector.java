package jscl.math.numeric;

import jscl.util.ArrayComparator;

public class NumericVector extends Numeric {
    protected final Numeric element[];
    protected final int n;

    public NumericVector(Numeric element[]) {
        this.element=element;
        n=element.length;
    }

    public Numeric[] elements() {
        return element;
    }

    public NumericVector add(NumericVector vector) {
        NumericVector v=(NumericVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=element[i].add(vector.element[i]);
        return v;
    }

    public Numeric add(Numeric numeric) {
        if(numeric instanceof NumericVector) {
            return add((NumericVector)numeric);
        } else {
            return add(valueof(numeric));
        }
    }

    public NumericVector subtract(NumericVector vector) {
        NumericVector v=(NumericVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=element[i].subtract(vector.element[i]);
        return v;
    }

    public Numeric subtract(Numeric numeric) {
        if(numeric instanceof NumericVector) {
            return subtract((NumericVector)numeric);
        } else {
            return subtract(valueof(numeric));
        }
    }

    public Numeric multiply(Numeric numeric) {
        if(numeric instanceof NumericVector) {
            return scalarProduct((NumericVector)numeric);
        } else if(numeric instanceof NumericMatrix) {
            return ((NumericMatrix)numeric).transpose().multiply(this);
        } else {
            NumericVector v=(NumericVector)newinstance();
            for(int i=0;i<n;i++) v.element[i]=element[i].multiply((Numeric)numeric);
            return v;
        }
    }

    public Numeric divide(Numeric numeric) throws ArithmeticException {
        if(numeric instanceof NumericVector) {
            throw new ArithmeticException();
        } else if(numeric instanceof NumericMatrix) {
            return multiply(((NumericMatrix)numeric).inverse());
        } else {
            NumericVector v=(NumericVector)newinstance();
            for(int i=0;i<n;i++) {
                v.element[i]=element[i].divide((Numeric)numeric);
            }
            return v;
        }
    }

    public Numeric negate() {
        NumericVector v=(NumericVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=element[i].negate();
        return v;
    }

    public int signum() {
        for(int i=0;i<n;i++) {
            int c=element[i].signum();
            if(c<0) return -1;
            else if(c>0) return 1;
        }
        return 0;
    }

    public Numeric valueof(Numeric numeric) {
        if(numeric instanceof NumericVector ||  numeric instanceof NumericMatrix) {
            throw new ArithmeticException();
        } else {
            NumericVector v=(NumericVector)unity(n).multiply(numeric);
            return newinstance(v.element);
        }
    }

    public Numeric magnitude2() {
        return scalarProduct(this);
    }

    public Numeric scalarProduct(NumericVector vector) {
        Numeric a=JSCLDouble.valueOf(0);
        for(int i=0;i<n;i++) {
            a=a.add(element[i].multiply(vector.element[i]));
        }
        return a;
    }

    public Numeric log() {
        throw new ArithmeticException();
    }

    public Numeric exp() {
        throw new ArithmeticException();
    }

    public Numeric conjugate() {
        NumericVector v=(NumericVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=element[i].conjugate();
        return v;
    }

    public int compareTo(NumericVector vector) {
        return ArrayComparator.comparator.compare(element,vector.element);
    }

    public int compareTo(Numeric numeric) {
        if(numeric instanceof NumericVector) {
            return compareTo((NumericVector)numeric);
        } else {
            return compareTo(valueof(numeric));
        }
    }

    public static NumericVector unity(int dimension) {
        NumericVector v=new NumericVector(new Numeric[dimension]);
        for(int i=0;i<v.n;i++) {
            if(i==0) v.element[i]=JSCLDouble.valueOf(1);
            else v.element[i]=JSCLDouble.valueOf(0);
        }
        return v;
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("{");
        for(int i=0;i<n;i++) {
            buffer.append(element[i]).append(i<n-1?", ":"");
        }
        buffer.append("}");
        return buffer.toString();
    }

    protected NumericVector newinstance() {
        return newinstance(new Numeric[n]);
    }

    protected NumericVector newinstance(Numeric element[]) {
        return new NumericVector(element);
    }
}
