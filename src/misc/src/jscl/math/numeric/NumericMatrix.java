package jscl.math.numeric;

import jscl.util.ArrayComparator;

public class NumericMatrix extends Numeric {
    protected final Numeric element[][];
    protected final int n,p;

    public NumericMatrix(Numeric element[][]) {
        this.element=element;
        n=element.length;
        p=element.length>0?element[0].length:0;
    }

    public Numeric[][] elements() {
        return element;
    }

    public NumericMatrix add(NumericMatrix matrix) {
        NumericMatrix m=(NumericMatrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=element[i][j].add(matrix.element[i][j]);
            }
        }
        return m;
    }

    public Numeric add(Numeric numeric) {
        if(numeric instanceof NumericMatrix) {
            return add((NumericMatrix)numeric);
        } else {
            return add(valueof(numeric));
        }
    }

    public NumericMatrix subtract(NumericMatrix matrix) {
        NumericMatrix m=(NumericMatrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=element[i][j].subtract(matrix.element[i][j]);
            }
        }
        return m;
    }

    public Numeric subtract(Numeric numeric) {
        if(numeric instanceof NumericMatrix) {
            return subtract((NumericMatrix)numeric);
        } else {
            return subtract(valueof(numeric));
        }
    }

    public NumericMatrix multiply(NumericMatrix matrix) {
        if(p!=matrix.n) throw new ArithmeticException();
        NumericMatrix m=(NumericMatrix)newinstance(new Numeric[n][matrix.p]);
        for(int i=0;i<n;i++) {
            for(int j=0;j<matrix.p;j++) {
                m.element[i][j]=JSCLDouble.valueOf(0);
                for(int k=0;k<p;k++) {
                    m.element[i][j]=m.element[i][j].add(element[i][k].multiply(matrix.element[k][j]));
                }
            }
        }
        return m;
    }

    public Numeric multiply(Numeric numeric) {
        if(numeric instanceof NumericMatrix) {
            return multiply((NumericMatrix)numeric);
        } else if(numeric instanceof NumericVector) {
            NumericVector v=(NumericVector)((NumericVector)numeric).newinstance(new Numeric[n]);
            NumericVector v2=(NumericVector)numeric;
            if(p!=v2.n) throw new ArithmeticException();
            for(int i=0;i<n;i++) {
                v.element[i]=JSCLDouble.valueOf(0);
                for(int k=0;k<p;k++) {
                    v.element[i]=v.element[i].add(element[i][k].multiply(v2.element[k]));
                }
            }
            return v;
        } else {
            NumericMatrix m=(NumericMatrix)newinstance();
            for(int i=0;i<n;i++) {
                for(int j=0;j<p;j++) {
                    m.element[i][j]=element[i][j].multiply((Numeric)numeric);
                }
            }
            return m;
        }
    }

    public Numeric divide(Numeric numeric) throws ArithmeticException {
        if(numeric instanceof NumericMatrix) {
            return multiply(((NumericMatrix)numeric).inverse());
        } else if(numeric instanceof NumericVector) {
            throw new ArithmeticException();
        } else {
            NumericMatrix m=(NumericMatrix)newinstance();
            for(int i=0;i<n;i++) {
                for(int j=0;j<p;j++) {
                    m.element[i][j]=element[i][j].divide((Numeric)numeric);
                }
            }
            return m;
        }
    }

    public Numeric negate() {
        NumericMatrix m=(NumericMatrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=element[i][j].negate();
            }
        }
        return m;
    }

    public int signum() {
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                int c=element[i][j].signum();
                if(c<0) return -1;
                else if(c>0) return 1;
            }
        }
        return 0;
    }

    public Numeric valueof(Numeric numeric) {
        if(numeric instanceof NumericMatrix || numeric instanceof NumericVector) {
            throw new ArithmeticException();
        } else {
            NumericMatrix m=(NumericMatrix)identity(n,p).multiply(numeric);
            return newinstance(m.element);
        }
    }

    public Numeric[] vectors() {
        NumericVector v[]=new NumericVector[n];
        for(int i=0;i<n;i++) {
            v[i]=new NumericVector(new Numeric[p]);
            for(int j=0;j<p;j++) {
                v[i].element[j]=element[i][j];
            }
        }
        return v;
    }

    public Numeric transpose() {
        NumericMatrix m=(NumericMatrix)newinstance(new Numeric[p][n]);
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[j][i]=element[i][j];
            }
        }
        return m;
    }

    public Numeric trace() {
        Numeric s=JSCLDouble.valueOf(0);
        for(int i=0;i<n;i++) {
            s=s.add(element[i][i]);
        }
        return s;
    }

    public Numeric inverse() {
        NumericMatrix m=(NumericMatrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<n;j++) {
                m.element[i][j]=inverseElement(i,j);
            }
        }
        return m.transpose().divide(determinant());
    }

    Numeric inverseElement(int k, int l) {
        NumericMatrix m=(NumericMatrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<n;j++) {
                m.element[i][j]=i==k?JSCLDouble.valueOf(j==l?1:0):element[i][j];
            }
        }
        return m.determinant();
    }

    public Numeric determinant() {
        if(n>1) {
            Numeric a=JSCLDouble.valueOf(0);
            for(int i=0;i<n;i++) {
                if(element[i][0].signum()==0);
                else {
                    NumericMatrix m=(NumericMatrix)newinstance(new Numeric[n-1][n-1]);
                    for(int j=0;j<n-1;j++) {
                        for(int k=0;k<n-1;k++) m.element[j][k]=element[j<i?j:j+1][k+1];
                    }
                    if(i%2==0) a=a.add(element[i][0].multiply(m.determinant()));
                    else a=a.subtract(element[i][0].multiply(m.determinant()));
                }
            }
            return a;
        } else if(n>0) return element[0][0];
        else return JSCLDouble.valueOf(0);
    }

    public Numeric log() {
        throw new ArithmeticException();
    }

    public Numeric exp() {
        throw new ArithmeticException();
    }

    public Numeric conjugate() {
        NumericMatrix m=(NumericMatrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=element[i][j].conjugate();
            }
        }
        return m;
    }

    public int compareTo(NumericMatrix matrix) {
        return ArrayComparator.comparator.compare(vectors(),matrix.vectors());
    }

    public int compareTo(Numeric numeric) {
        if(numeric instanceof NumericMatrix) {
            return compareTo((NumericMatrix)numeric);
        } else {
            return compareTo(valueof(numeric));
        }
    }

    public static NumericMatrix identity(int dimension) {
        return identity(dimension,dimension);
    }

    public static NumericMatrix identity(int n, int p) {
        NumericMatrix m=new NumericMatrix(new Numeric[n][p]);
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                if(i==j) {
                    m.element[i][j]=JSCLDouble.valueOf(1);
                } else {
                    m.element[i][j]=JSCLDouble.valueOf(0);
                }
            }
        }
        return m;
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("{");
        for(int i=0;i<n;i++) {
            buffer.append("{");
            for(int j=0;j<p;j++) {
                buffer.append(element[i][j]).append(j<p-1?", ":"");
            }
            buffer.append("}").append(i<n-1?",\n":"");
        }
        buffer.append("}");
        return buffer.toString();
    }

    protected NumericMatrix newinstance() {
        return newinstance(new Numeric[n][p]);
    }

    protected NumericMatrix newinstance(Numeric element[][]) {
        return new NumericMatrix(element);
    }
}
