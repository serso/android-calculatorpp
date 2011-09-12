package jscl.math.polynomial;

import java.util.Iterator;
import jscl.math.Literal;
import jscl.math.NotDivisibleException;
import jscl.math.Variable;
import jscl.math.function.Frac;
import jscl.math.function.Pow;
import jscl.mathml.MathML;

public class Monomial implements Comparable {
    public static final Ordering lexicographic=Lexicographic.ordering;
    public static final Ordering totalDegreeLexicographic=TotalDegreeLexicographic.ordering;
    public static final Ordering degreeReverseLexicographic=DegreeReverseLexicographic.ordering;
    public static final Ordering iteratorOrdering=totalDegreeLexicographic;
    final Variable unknown[];
    final Ordering ordering;
    final int element[];
    int degree;

    Monomial(Variable unknown[], Ordering ordering) {
        this(unknown.length,unknown,ordering);
    }

    Monomial(int length, Variable unknown[], Ordering ordering) {
        this.unknown=unknown;
        this.ordering=ordering;
        element=new int[length];
    }

    public static Ordering kthElimination(int k) {
        return new KthElimination(k,1);
    }

    public Variable[] unknown() {
        return unknown;
    }

    public Ordering ordering() {
        return ordering;
    }

    public Monomial multiply(Monomial monomial) {
        Monomial m=newinstance();
        for(int i=0;i<unknown.length;i++) {
            m.element[i]=element[i]+monomial.element[i];
        }
        m.degree=degree+monomial.degree;
        return m;
    }

    public boolean multiple(Monomial monomial) {
        return multiple(monomial,false);
    }

    public boolean multiple(Monomial monomial, boolean strict) {
        boolean equal=true;
        for(int i=0;i<unknown.length;i++) {
            if(element[i]<monomial.element[i]) return false;
            equal&=element[i]==monomial.element[i];
        }
        return strict?!equal:true;
    }

    public Monomial divide(Monomial monomial) throws ArithmeticException {
        Monomial m=newinstance();
        for(int i=0;i<unknown.length;i++) {
            int n=element[i]-monomial.element[i];
            if(n<0) throw new NotDivisibleException();
            m.element[i]=n;
        }
        m.degree=degree-monomial.degree;
        return m;
    }

    public Monomial gcd(Monomial monomial) {
        Monomial m=newinstance();
        for(int i=0;i<unknown.length;i++) {
            int n=Math.min(element[i],monomial.element[i]);
            m.element[i]=n;
            m.degree+=n;
        }
        return m;
    }

    public Monomial scm(Monomial monomial) {
        Monomial m=newinstance();
        for(int i=0;i<unknown.length;i++) {
            int n=Math.max(element[i],monomial.element[i]);
            m.element[i]=n;
            m.degree+=n;
        }
        return m;
    }

    public int degree() {
        return degree;
    }

    public Monomial valueof(Monomial monomial) {
        Monomial m=newinstance();
        System.arraycopy(monomial.element, 0, m.element, 0, m.element.length);
        m.degree=monomial.degree;
        return m;
    }

    public Monomial valueof(Literal literal) {
        Monomial m=newinstance();
        m.init(literal);
        return m;
    }

    public Literal literalValue() {
        return Literal.valueOf(this);
    }

    public int element(int n) {
        return element[n];
    }

    public Iterator iterator() {
        return iterator(newinstance());
    }

    public Iterator iterator(Monomial beginning) {
        return new MonomialIterator(beginning,this);
    }

    public Iterator divisor() {
        return divisor(newinstance());
    }

    public Iterator divisor(Monomial beginning) {
        return new MonomialDivisor(beginning,this);
    }

    static Monomial factory(Variable unknown[]) {
        return factory(unknown,lexicographic);
    }

    static Monomial factory(Variable unknown[], Ordering ordering) {
        return factory(unknown,ordering,0);
    }

    static Monomial factory(Variable unknown[], Ordering ordering, int power_size) {
        switch(power_size) {
        case Basis.POWER_8:
            return new SmallMonomial(unknown,small(ordering));
        case Basis.POWER_2:
            return new BooleanMonomial(unknown,small(ordering));
        case Basis.POWER_2_DEFINED:
            return new DefinedBooleanMonomial(unknown,small(ordering));
        default:
            return new Monomial(unknown,ordering);
        }
    }

    static Ordering small(Ordering ordering) {
        if(ordering==lexicographic) return SmallMonomial.lexicographic;
        else if(ordering==totalDegreeLexicographic) return SmallMonomial.totalDegreeLexicographic;
        else if(ordering==degreeReverseLexicographic) return SmallMonomial.degreeReverseLexicographic;
        else throw new UnsupportedOperationException();
    }

    public int compareTo(Monomial monomial) {
        return ordering.compare(this,monomial);
    }

    public int compareTo(Object o) {
        return compareTo((Monomial)o);
    }

    void init(Literal literal) {
        int s=literal.size();
        for(int i=0;i<s;i++) {
            Variable v=literal.variable(i);
            int c=literal.power(i);
            int n=variable(v,unknown);
            if(n<unknown.length) put(n,c);
        }
    }

    static int variable(Variable v, Variable unknown[]) {
        int i=0;
        for(;i<unknown.length;i++) if(unknown[i].equals(v)) break;
        return i;
    }

    void put(int n, int integer) {
        element[n]+=integer;
        degree+=integer;
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        if(degree==0) buffer.append("1");
        boolean b=false;
        for(int i=0;i<unknown.length;i++) {
            int c=element(i);
            if(c>0) {
                if(b) buffer.append("*");
                else b=true;
                Variable v=unknown[i];
                if(c==1) buffer.append(v);
                else {
                    if(v instanceof Frac || v instanceof Pow) {
                        buffer.append("(").append(v).append(")");
                    } else buffer.append(v);
                    buffer.append("^").append(c);
                }
            }
        }
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        if(degree==0) {
            MathML e1=element.element("mn");
            e1.appendChild(element.text("1"));
            element.appendChild(e1);
        }
        for(int i=0;i<unknown.length;i++) {
            int c=element(i);
            if(c>0) {
                unknown[i].toMathML(element,new Integer(c));
            }
        }
    }

    protected Monomial newinstance() {
        return new Monomial(element.length,unknown,ordering);
    }
}

class MonomialIterator implements Iterator {
    static final Ordering ordering=Monomial.iteratorOrdering;
    Monomial monomial;
    Monomial current;
    boolean carry;

    MonomialIterator(Monomial beginning, Monomial monomial) {
        this.monomial=monomial;
        current=monomial.valueof(beginning);
        if(ordering.compare(current,monomial)>0) carry=true;
    }

    public boolean hasNext() {
        return !carry;
    }

    public Object next() {
        Monomial m=monomial.valueof(current);
        if(ordering.compare(current,monomial)<0) increment();
        else carry=true;
        return m;
    }

    void increment() {
        int s=0;
        int n=0;
        while(n<current.element.length && current.element[n]==0) n++;
        if(n<current.element.length) {
            s=current.element[n];
            current.element[n]=0;
            n++;
        }
        if(n<current.element.length) {
            current.element[n]++;
            fill(s-1);
        } else {
            current.degree++;
            fill(s+1);
        }
    }

    private void fill(int s) {
        current.element[0]=s;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}

class MonomialDivisor extends MonomialIterator {
    MonomialDivisor(Monomial beginning, Monomial monomial) {
        super(beginning,monomial);
        if(hasNext()) seek();
    }

    void seek() {
        int n=current.element.length;
        while(n>0) {
            n--;
            if(current.element[n]>monomial.element[n]) break;
        }
        int p=n;
        while(n>0) {
            n--;
            current.element[p]+=current.element[n];
            current.element[n]=0;
        }
        if(p<current.element.length && current.element[p]>monomial.element[p]) increment();
    }

    void increment() {
        int s=0;
        int n=0;
        while(n<current.element.length && current.element[n]==0) n++;
        if(n<current.element.length) {
            s=current.element[n];
            current.element[n]=0;
            n++;
        }
        while(n<current.element.length && current.element[n]==monomial.element[n]) {
            s+=current.element[n];
            current.element[n]=0;
            n++;
        }
        if(n<current.element.length) {
            current.element[n]++;
            fill(s-1);
        } else {
            current.degree++;
            fill(s+1);
        }
    }

    private void fill(int s) {
        for(int i=0;i<current.element.length;i++) {
            int d=Math.min(monomial.element[i]-current.element[i],s);
            current.element[i]+=d;
            s-=d;
        }
    }
}

class Lexicographic extends Ordering {
    public static final Ordering ordering=new Lexicographic();

    Lexicographic() {}

    public int compare(Monomial m1, Monomial m2) {
        int c1[]=m1.element;
        int c2[]=m2.element;
        int n=c1.length;
        for(int i=n-1;i>=0;i--) {
            if(c1[i]<c2[i]) return -1;
            else if(c1[i]>c2[i]) return 1;
        }
        return 0;
    }
}

class TotalDegreeLexicographic extends Lexicographic implements DegreeOrdering {
    public static final Ordering ordering=new TotalDegreeLexicographic();

    TotalDegreeLexicographic() {}

    public int compare(Monomial m1, Monomial m2) {
        if(m1.degree<m2.degree) return -1;
        else if(m1.degree>m2.degree) return 1;
        else return super.compare(m1,m2);
    }
}

class DegreeReverseLexicographic extends Ordering implements DegreeOrdering {
    public static final Ordering ordering=new DegreeReverseLexicographic();

    DegreeReverseLexicographic() {}

    public int compare(Monomial m1, Monomial m2) {
        if(m1.degree<m2.degree) return -1;
        else if(m1.degree>m2.degree) return 1;
        else {
            int c1[]=m1.element;
            int c2[]=m2.element;
            int n=c1.length;
            for(int i=0;i<n;i++) {
                if(c1[i]>c2[i]) return -1;
                else if(c1[i]<c2[i]) return 1;
            }
            return 0;
        }
    }
}

class KthElimination extends Ordering {
    final int k;

    KthElimination(int k, int direction) {
        this.k=k;
    }

    public int compare(Monomial m1, Monomial m2) {
        int c1[]=m1.element;
        int c2[]=m2.element;
        int n=c1.length;
        int k=n-this.k;
        for(int i=n-1;i>=k;i--) {
            if(c1[i]<c2[i]) return -1;
            else if(c1[i]>c2[i]) return 1;
        }
        return DegreeReverseLexicographic.ordering.compare(m1,m2);
    }
}
