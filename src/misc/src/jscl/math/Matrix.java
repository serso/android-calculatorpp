package jscl.math;

import jscl.math.function.Conjugate;
import jscl.math.function.Frac;
import jscl.math.function.trigonometric.Cos;
import jscl.math.function.trigonometric.Sin;
import jscl.mathml.MathML;
import jscl.util.ArrayComparator;

public class Matrix extends Generic {
    protected final Generic element[][];
    protected final int n,p;

    public Matrix(Generic element[][]) {
        this.element=element;
        n=element.length;
        p=element.length>0?element[0].length:0;
    }

    public Generic[][] elements() {
        return element;
    }

    public Matrix add(Matrix matrix) {
        Matrix m=(Matrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=element[i][j].add(matrix.element[i][j]);
            }
        }
        return m;
    }

    public Generic add(Generic generic) {
        if(generic instanceof Matrix) {
            return add((Matrix)generic);
        } else {
            return add(valueof(generic));
        }
    }

    public Matrix subtract(Matrix matrix) {
        Matrix m=(Matrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=element[i][j].subtract(matrix.element[i][j]);
            }
        }
        return m;
    }

    public Generic subtract(Generic generic) {
        if(generic instanceof Matrix) {
            return subtract((Matrix)generic);
        } else {
            return subtract(valueof(generic));
        }
    }

    public static boolean product(Generic a, Generic b) {
        return (a instanceof Matrix && b instanceof Matrix) || (a instanceof Matrix && b instanceof JSCLVector) || (a instanceof JSCLVector && b instanceof Matrix);
    }

    public Matrix multiply(Matrix matrix) {
        if(p!=matrix.n) throw new ArithmeticException();
        Matrix m=(Matrix)newinstance(new Generic[n][matrix.p]);
        for(int i=0;i<n;i++) {
            for(int j=0;j<matrix.p;j++) {
                m.element[i][j]=JSCLInteger.valueOf(0);
                for(int k=0;k<p;k++) {
                    m.element[i][j]=m.element[i][j].add(element[i][k].multiply(matrix.element[k][j]));
                }
            }
        }
        return m;
    }

    public Generic multiply(Generic generic) {
        if(generic instanceof Matrix) {
            return multiply((Matrix)generic);
        } else if(generic instanceof JSCLVector) {
            JSCLVector v=(JSCLVector)((JSCLVector)generic).newinstance(new Generic[n]);
            JSCLVector v2=(JSCLVector)generic;
            if(p!=v2.n) throw new ArithmeticException();
            for(int i=0;i<n;i++) {
                v.element[i]=JSCLInteger.valueOf(0);
                for(int k=0;k<p;k++) {
                    v.element[i]=v.element[i].add(element[i][k].multiply(v2.element[k]));
                }
            }
            return v;
        } else {
            Matrix m=(Matrix)newinstance();
            for(int i=0;i<n;i++) {
                for(int j=0;j<p;j++) {
                    m.element[i][j]=element[i][j].multiply(generic);
                }
            }
            return m;
        }
    }

    public Generic divide(Generic generic) throws ArithmeticException {
        if(generic instanceof Matrix) {
            return multiply(((Matrix)generic).inverse());
        } else if(generic instanceof JSCLVector) {
            throw new ArithmeticException();
        } else {
            Matrix m=(Matrix)newinstance();
            for(int i=0;i<n;i++) {
                for(int j=0;j<p;j++) {
                    try {
                        m.element[i][j]=element[i][j].divide(generic);
                    } catch (NotDivisibleException e) {
                        m.element[i][j]=new Frac(element[i][j],generic).evaluate();
                    }
                }
            }
            return m;
        }
    }

    public Generic gcd(Generic generic) {
        return null;
    }

    public Generic gcd() {
        return null;
    }

    public Generic negate() {
        Matrix m=(Matrix)newinstance();
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

    public int degree() {
        return 0;
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        Matrix m=(Matrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=element[i][j].antiderivative(variable);
            }
        }
        return m;
    }

    public Generic derivative(Variable variable) {
        Matrix m=(Matrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=element[i][j].derivative(variable);
            }
        }
        return m;
    }

    public Generic substitute(Variable variable, Generic generic) {
        Matrix m=(Matrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=element[i][j].substitute(variable,generic);
            }
        }
        return m;
    }

    public Generic expand() {
        Matrix m=(Matrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=element[i][j].expand();
            }
        }
        return m;
    }

    public Generic factorize() {
        Matrix m=(Matrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=element[i][j].factorize();
            }
        }
        return m;
    }

    public Generic elementary() {
        Matrix m=(Matrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=element[i][j].elementary();
            }
        }
        return m;
    }

    public Generic simplify() {
        Matrix m=(Matrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=element[i][j].simplify();
            }
        }
        return m;
    }

    public Generic numeric() {
        return new NumericWrapper(this);
    }

    public Generic valueof(Generic generic) {
        if(generic instanceof Matrix || generic instanceof JSCLVector) {
            throw new ArithmeticException();
        } else {
            Matrix m=(Matrix)identity(n,p).multiply(generic);
                        return newinstance(m.element);
        }
    }

    public Generic[] sumValue() {
        return new Generic[] {this};
    }

    public Generic[] productValue() throws NotProductException {
        return new Generic[] {this};
    }

    public Power powerValue() throws NotPowerException {
        return new Power(this,1);
    }

    public Expression expressionValue() throws NotExpressionException {
        throw new NotExpressionException();
    }

    public JSCLInteger integerValue() throws NotIntegerException {
        throw new NotIntegerException();
    }

    public Variable variableValue() throws NotVariableException {
        throw new NotVariableException();
    }

    public Variable[] variables() {
        return null;
    }

    public boolean isPolynomial(Variable variable) {
        return false;
    }

    public boolean isConstant(Variable variable) {
        return false;
    }

    public Generic[] vectors() {
        JSCLVector v[]=new JSCLVector[n];
        for(int i=0;i<n;i++) {
            v[i]=new JSCLVector(new Generic[p]);
            for(int j=0;j<p;j++) {
                v[i].element[j]=element[i][j];
            }
        }
        return v;
    }

    public Generic tensorProduct(Matrix matrix) {
        Matrix m=(Matrix)newinstance(new Generic[n*matrix.n][p*matrix.p]);
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                for(int k=0;k<matrix.n;k++) {
                    for(int l=0;l<matrix.p;l++) {
                        m.element[i*matrix.n+k][j*matrix.p+l]=element[i][j].multiply(matrix.element[k][l]);
                    }
                }
            }
        }
        return m;
    }

    public Generic transpose() {
        Matrix m=(Matrix)newinstance(new Generic[p][n]);
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[j][i]=element[i][j];
            }
        }
        return m;
    }

    public Generic trace() {
        Generic s=JSCLInteger.valueOf(0);
        for(int i=0;i<n;i++) {
            s=s.add(element[i][i]);
        }
        return s;
    }

    public Generic inverse() {
        Matrix m=(Matrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<n;j++) {
                m.element[i][j]=inverseElement(i,j);
            }
        }
        return m.transpose().divide(determinant());
    }

    Generic inverseElement(int k, int l) {
        Matrix m=(Matrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<n;j++) {
                m.element[i][j]=i==k?JSCLInteger.valueOf(j==l?1:0):element[i][j];
            }
        }
        return m.determinant();
    }

    public Generic determinant() {
        if(n>1) {
            Generic a=JSCLInteger.valueOf(0);
            for(int i=0;i<n;i++) {
                if(element[i][0].signum()==0);
                else {
                    Matrix m=(Matrix)newinstance(new Generic[n-1][n-1]);
                    for(int j=0;j<n-1;j++) {
                        for(int k=0;k<n-1;k++) m.element[j][k]=element[j<i?j:j+1][k+1];
                    }
                    if(i%2==0) a=a.add(element[i][0].multiply(m.determinant()));
                    else a=a.subtract(element[i][0].multiply(m.determinant()));
                }
            }
            return a;
        } else if(n>0) return element[0][0];
        else return JSCLInteger.valueOf(0);
    }

    public Generic conjugate() {
        Matrix m=(Matrix)newinstance();
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                m.element[i][j]=new Conjugate(element[i][j]).evaluate();
            }
        }
        return m;
    }

    public int compareTo(Matrix matrix) {
        return ArrayComparator.comparator.compare(vectors(),matrix.vectors());
    }

    public int compareTo(Generic generic) {
        if(generic instanceof Matrix) {
            return compareTo((Matrix)generic);
        } else {
            return compareTo(valueof(generic));
        }
    }

    public static Matrix identity(int dimension) {
            return identity(dimension,dimension);
    }

    public static Matrix identity(int n, int p) {
        Matrix m=new Matrix(new Generic[n][p]);
        for(int i=0;i<n;i++) {
            for(int j=0;j<p;j++) {
                if(i==j) {
                    m.element[i][j]=JSCLInteger.valueOf(1);
                } else {
                    m.element[i][j]=JSCLInteger.valueOf(0);
                }
            }
        }
        return m;
    }

    public static Matrix frame(JSCLVector vector[]) {
        Matrix m=new Matrix(new Generic[vector.length>0?vector[0].n:0][vector.length]);
        for(int i=0;i<m.n;i++) {
            for(int j=0;j<m.p;j++) {
                m.element[i][j]=vector[j].element[i];
            }
        }
        return m;
    }

    public static Matrix rotation(int dimension, int plane, Generic angle) {
        return rotation(dimension,plane,2,angle);
    }

    public static Matrix rotation(int dimension, int axis1, int axis2, Generic angle) {
        Matrix m=new Matrix(new Generic[dimension][dimension]);
        for(int i=0;i<m.n;i++) {
            for(int j=0;j<m.p;j++) {
                if(i==axis1 && j==axis1) {
                    m.element[i][j]=new Cos(angle).evaluate();
                } else if(i==axis1 && j==axis2) {
                    m.element[i][j]=new Sin(angle).evaluate().negate();
                } else if(i==axis2 && j==axis1) {
                    m.element[i][j]=new Sin(angle).evaluate();
                } else if(i==axis2 && j==axis2) {
                    m.element[i][j]=new Cos(angle).evaluate();
                } else if(i==j) {
                    m.element[i][j]=JSCLInteger.valueOf(1);
                } else {
                    m.element[i][j]=JSCLInteger.valueOf(0);
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

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("new NumericMatrix(new Numeric[][] {");
        for(int i=0;i<n;i++) {
            buffer.append("{");
            for(int j=0;j<p;j++) {
                buffer.append(element[i][j].toJava()).append(j<p-1?", ":"");
            }
            buffer.append("}").append(i<n-1?", ":"");
        }
        buffer.append("})");
        return buffer.toString();
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

    protected void bodyToMathML(MathML e0) {
        MathML e1=e0.element("mfenced");
        MathML e2=e0.element("mtable");
        for(int i=0;i<n;i++) {
            MathML e3=e0.element("mtr");
            for(int j=0;j<p;j++) {
                MathML e4=e0.element("mtd");
                element[i][j].toMathML(e4,null);
                e3.appendChild(e4);
            }
            e2.appendChild(e3);
        }
        e1.appendChild(e2);
        e0.appendChild(e1);
    }

    protected Generic newinstance() {
        return newinstance(new Generic[n][p]);
    }

    protected Generic newinstance(Generic element[][]) {
        return new Matrix(element);
    }
}
