package jscl.math.polynomial;

import jscl.math.Generic;

public class Term implements Comparable {
    final Monomial monomial;
    final Generic coef;

    public Term(Monomial monomial, Generic coef) {
        this.monomial = monomial;
        this.coef = coef;
    }

    public Term subtract(Term term) {
        return new Term(monomial, coef.subtract(term.coef));
    }

    public Term multiply(Generic generic) {
        return new Term(monomial, coef.multiply(generic));
    }

    public Term multiply(Monomial monomial, Generic generic) {
        return new Term(this.monomial.multiply(monomial), coef.multiply(generic));
    }

    public Term multiply(Monomial monomial) {
        return new Term(this.monomial.multiply(monomial), coef);
    }

    public Term divide(Generic generic) {
        return new Term(monomial, coef.divide(generic));
    }

    public Term divide(Monomial monomial) {
        return new Term(this.monomial.divide(monomial), coef);
    }

    public Term negate() {
        return new Term(monomial, coef.negate());
    }

    public int signum() {
        return coef.signum();
    }

    public Monomial monomial() {
        return monomial;
    }

    public Generic coef() {
        return coef;
    }

    public int compareTo(Term term) {
        return monomial.compareTo(term.monomial);
    }

    public int compareTo(Object o) {
        return compareTo((Term) o);
    }

    public String toString() {
        return "(" + coef + ", " + monomial + ")";
    }
}
