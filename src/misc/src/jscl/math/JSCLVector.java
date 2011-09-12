package jscl.math;

import jscl.math.function.Conjugate;
import jscl.math.function.Frac;
import jscl.mathml.MathML;
import jscl.util.ArrayComparator;

public class JSCLVector extends Generic {
    protected final Generic element[];
    protected final int n;

    public JSCLVector(Generic element[]) {
        this.element=element;
        n=element.length;
    }

    public Generic[] elements() {
        return element;
    }

    public JSCLVector add(JSCLVector vector) {
        JSCLVector v=(JSCLVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=element[i].add(vector.element[i]);
        return v;
    }

    public Generic add(Generic generic) {
        if(generic instanceof JSCLVector) {
            return add((JSCLVector)generic);
        } else {
            return add(valueof(generic));
        }
    }

    public JSCLVector subtract(JSCLVector vector) {
        JSCLVector v=(JSCLVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=element[i].subtract(vector.element[i]);
        return v;
    }

    public Generic subtract(Generic generic) {
        if(generic instanceof JSCLVector) {
            return subtract((JSCLVector)generic);
        } else {
            return subtract(valueof(generic));
        }
    }

    public Generic multiply(Generic generic) {
        if(generic instanceof JSCLVector) {
            return scalarProduct((JSCLVector)generic);
        } else if(generic instanceof Matrix) {
            return ((Matrix)generic).transpose().multiply(this);
        } else {
            JSCLVector v=(JSCLVector)newinstance();
            for(int i=0;i<n;i++) v.element[i]=element[i].multiply(generic);
            return v;
        }
    }

    public Generic divide(Generic generic) throws ArithmeticException {
        if(generic instanceof JSCLVector) {
            throw new ArithmeticException();
        } else if(generic instanceof Matrix) {
            return multiply(((Matrix)generic).inverse());
        } else {
            JSCLVector v=(JSCLVector)newinstance();
            for(int i=0;i<n;i++) {
                try {
                    v.element[i]=element[i].divide(generic);
                } catch (NotDivisibleException e) {
                    v.element[i]=new Frac(element[i],generic).evaluate();
                }
            }
            return v;
        }
    }

    public Generic gcd(Generic generic) {
        return null;
    }

    public Generic gcd() {
        return null;
    }

    public Generic negate() {
        JSCLVector v=(JSCLVector)newinstance();
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

    public int degree() {
        return 0;
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        JSCLVector v=(JSCLVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=element[i].antiderivative(variable);
        return v;
    }

    public Generic derivative(Variable variable) {
        JSCLVector v=(JSCLVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=element[i].derivative(variable);
        return v;
    }

    public Generic substitute(Variable variable, Generic generic) {
        JSCLVector v=(JSCLVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=element[i].substitute(variable,generic);
        return v;
    }

    public Generic expand() {
        JSCLVector v=(JSCLVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=element[i].expand();
        return v;
    }

    public Generic factorize() {
        JSCLVector v=(JSCLVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=element[i].factorize();
        return v;
    }

    public Generic elementary() {
        JSCLVector v=(JSCLVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=element[i].elementary();
        return v;
    }

    public Generic simplify() {
        JSCLVector v=(JSCLVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=element[i].simplify();
        return v;
    }

    public Generic numeric() {
        return new NumericWrapper(this);
    }

    public Generic valueof(Generic generic) {
        if(generic instanceof JSCLVector ||  generic instanceof Matrix) {
            throw new ArithmeticException();
        } else {
            JSCLVector v=(JSCLVector)unity(n).multiply(generic);
            return newinstance(v.element);
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

    public Generic magnitude2() {
        return scalarProduct(this);
    }

    public Generic scalarProduct(JSCLVector vector) {
        Generic a=JSCLInteger.valueOf(0);
        for(int i=0;i<n;i++) {
            a=a.add(element[i].multiply(vector.element[i]));
        }
        return a;
    }

    public JSCLVector vectorProduct(JSCLVector vector) {
        JSCLVector v=(JSCLVector)newinstance();
        Generic m[][]={
            {JSCLInteger.valueOf(0),element[2].negate(),element[1]},
            {element[2],JSCLInteger.valueOf(0),element[0].negate()},
            {element[1].negate(),element[0],JSCLInteger.valueOf(0)}
        };
        JSCLVector v2=(JSCLVector)new Matrix(m).multiply(vector);
        for(int i=0;i<n;i++) v.element[i]=i<v2.n?v2.element[i]:JSCLInteger.valueOf(0);
        return v;
    }

    public JSCLVector complexProduct(JSCLVector vector) {
        return product(new Clifford(0,1).operator(),vector);
    }

    public JSCLVector quaternionProduct(JSCLVector vector) {
        return product(new Clifford(0,2).operator(),vector);
    }

    public JSCLVector geometricProduct(JSCLVector vector, int algebra[]) {
        return product(new Clifford(algebra==null?new int[] {Clifford.log2e(n),0}:algebra).operator(),vector);
    }

    JSCLVector product(int product[][], JSCLVector vector) {
        JSCLVector v=(JSCLVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=JSCLInteger.valueOf(0);
        for(int i=0;i<n;i++) {
            for(int j=0;j<n;j++) {
                Generic a=element[i].multiply(vector.element[j]);
                int k=Math.abs(product[i][j])-1;
                v.element[k]=v.element[k].add(product[i][j]<0?a.negate():a);
            }
        }
        return v;
    }

    public Generic divergence(Variable variable[]) {
        Generic a=JSCLInteger.valueOf(0);
        for(int i=0;i<n;i++) a=a.add(element[i].derivative(variable[i]));
        return a;
    }

    public JSCLVector curl(Variable variable[]) {
        JSCLVector v=(JSCLVector)newinstance();
        v.element[0]=element[2].derivative(variable[1]).subtract(element[1].derivative(variable[2]));
        v.element[1]=element[0].derivative(variable[2]).subtract(element[2].derivative(variable[0]));
        v.element[2]=element[1].derivative(variable[0]).subtract(element[0].derivative(variable[1]));
        for(int i=3;i<n;i++) v.element[i]=element[i];
        return v;
    }

    public Matrix jacobian(Variable variable[]) {
        Matrix m=new Matrix(new Generic[n][variable.length]);
        for(int i=0;i<n;i++) {
            for(int j=0;j<variable.length;j++) {
                m.element[i][j]=element[i].derivative(variable[j]);
            }
        }
        return m;
    }

    public Generic del(Variable variable[], int algebra[]) {
        return differential(new Clifford(algebra==null?new int[] {Clifford.log2e(n),0}:algebra).operator(),variable);
    }

    JSCLVector differential(int product[][], Variable variable[]) {
        JSCLVector v=(JSCLVector)newinstance();
        for(int i=0;i<n;i++) v.element[i]=JSCLInteger.valueOf(0);
        int l=Clifford.log2e(n);
        for(int i=1;i<=l;i++) {
            for(int j=0;j<n;j++) {
                Generic a=element[j].derivative(variable[i-1]);
                int k=Math.abs(product[i][j])-1;
                v.element[k]=v.element[k].add(product[i][j]<0?a.negate():a);
            }
        }
        return v;
    }

    public Generic conjugate() {
        JSCLVector v=(JSCLVector)newinstance();
        for(int i=0;i<n;i++) {
            v.element[i]=new Conjugate(element[i]).evaluate();
        }
        return v;
    }

    public int compareTo(JSCLVector vector) {
        return ArrayComparator.comparator.compare(element,vector.element);
    }

    public int compareTo(Generic generic) {
        if(generic instanceof JSCLVector) {
            return compareTo((JSCLVector)generic);
        } else {
            return compareTo(valueof(generic));
        }
    }

    public static JSCLVector unity(int dimension) {
        JSCLVector v=new JSCLVector(new Generic[dimension]);
        for(int i=0;i<v.n;i++) {
            if(i==0) v.element[i]=JSCLInteger.valueOf(1);
            else v.element[i]=JSCLInteger.valueOf(0);
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

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("new NumericVector(new Numeric[] {");
        for(int i=0;i<n;i++) {
            buffer.append(element[i].toJava()).append(i<n-1?", ":"");
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
            MathML e4=e0.element("mtd");
            element[i].toMathML(e4,null);
            e3.appendChild(e4);
            e2.appendChild(e3);
        }
        e1.appendChild(e2);
        e0.appendChild(e1);
    }

    protected Generic newinstance() {
        return newinstance(new Generic[n]);
    }

    protected Generic newinstance(Generic element[]) {
        return new JSCLVector(element);
    }
}

class Clifford {
    int p,n;
    int operator[][];

    Clifford(int algebra[]) {
        this(algebra[0],algebra[1]);
    }

    Clifford(int p, int q) {
        this.p=p;
        n=p+q;
        int m=1<<n;
        operator=new int[m][m];
        for(int i=0;i<m;i++) {
            for(int j=0;j<m;j++) {
                int a=combination(i,n);
                int b=combination(j,n);
                int c=a^b;
                int l=location(c,n);
                boolean s=sign(a,b);
                int k=l+1;
                operator[i][j]=s?-k:k;
            }
        }
    }

    boolean sign(int a, int b) {
        boolean s=false;
        for(int i=0;i<n;i++) {
            if((b&(1<<i))>0) {
                for(int j=i;j<n;j++) {
                    if((a&(1<<j))>0 && (j>i || i>=p)) s=!s;
                }
            }
        }
        return s;
    }

    static int combination(int l, int n) {
        if(n<=2) return l;
        int b[]=new int[1];
        int l1=decimation(l,n,b);
        int c=combination(l1,n-1);
        return (c<<1)+b[0];
    }

    static int location(int c, int n) {
        if(n<=2) return c;
        int c1=c>>1;
        int b=c&1;
        int l1=location(c1,n-1);
        return dilatation(l1,n,new int[] {b});
    }

    static int decimation(int l, int n, int b[]) {
        int p=grade(l,n-1,1);
        int p1=(p+1)>>1;
        b[0]=p&1;
        return l-sum(p1,n-1);
    }

    static int dilatation(int l, int n, int b[]) {
        int p1=grade(l,n-1);
        return l+sum(p1+b[0],n-1);
    }

    static int grade(int l, int n) {
        return grade(l,n,0);
    }

    static int grade(int l, int n, int d) {
        int s=0, p=0;
        while(true) {
            s+=binomial(n,p>>d);
            if(s<=l) p++;
            else break;
        }
        return p;
    }

    static int sum(int p, int n) {
        int q=0, s=0;
        while(q<p) s+=binomial(n,q++);
        return s;
    }

    static int binomial(int n, int p) {
        int a=1, b=1;
        for(int i=n-p+1;i<=n;i++) a*=i;
        for(int i=2;i<=p;i++) b*=i;
        return a/b;
    }

    static int log2e(int n) {
        int i;
        for(i=0;n>1;n>>=1) i++;
        return i;
    }

    int[][] operator() {
        return operator;
    }
}
