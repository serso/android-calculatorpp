package jscl.math.polynomial;

import jscl.math.Variable;

class DefinedBooleanMonomial extends BooleanMonomial {
    DefinedBooleanMonomial(Variable unknown[], Ordering ordering) {
        super(unknown,ordering);
    }

    DefinedBooleanMonomial(int length, Variable unknown[], Ordering ordering) {
        super(length,unknown,ordering);
    }

    public Monomial multiply(Monomial monomial) {
        Monomial m=newinstance();
        for(int i=0;i<unknown.length;i++) {
            int q=i>>log2p;
            int r=(i&pmask)<<log2n;
            int a=(element[q]>>r)&nmask;
            int b=(monomial.element[q]>>r)&nmask;
            int c=a+b;
            if(c>1) c=1;
            m.element[q]|=c<<r;
            m.degree+=c;
        }
        return m;
    }

    protected Monomial newinstance() {
        return new DefinedBooleanMonomial(element.length,unknown,ordering);
    }
}
