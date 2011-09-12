package jscl.math.polynomial.groebner;

import jscl.math.polynomial.Monomial;
import jscl.math.polynomial.Polynomial;

class Pair implements Comparable {
    final Polynomial polynomial[];
    final Monomial monomial[];
    final Monomial scm;
    final int sugar;
    boolean coprime;
    boolean reduction;
    Polynomial principal;

    Pair(Polynomial p1, Polynomial p2) {
        this(new Polynomial[] {p1,p2});
        coprime=monomial[0].gcd(monomial[1]).degree()==0;
        int index[]=monomial[0].compareTo(monomial[1])<0?new int[] {0,1}:new int[] {1,0};
        reduction=monomial[index[1]].multiple(monomial[index[0]]);
        principal=polynomial[index[1]];
    }

    Pair(Polynomial polynomial[]) {
        this.polynomial=polynomial;
        monomial=new Monomial[] {polynomial[0].head().monomial(),polynomial[1].head().monomial()};
        scm=monomial[0].scm(monomial[1]);
        sugar=Math.max(polynomial[0].sugar()-polynomial[0].degree(),polynomial[1].sugar()-polynomial[1].degree())+scm.degree();
    }

    public int compareTo(Pair pair) {
        int c=scm.compareTo(pair.scm);
        if(c<0) return -1;
        else if(c>0) return 1;
        else {
            c=polynomial[1].index()-pair.polynomial[1].index();
            if(c<0) return -1;
            else if(c>0) return 1;
            else {
                c=polynomial[0].index()-pair.polynomial[0].index();
                if(c<0) return -1;
                else if(c>0) return 1;
                else return 0;
            }
        }
    }

    public int compareTo(Object o) {
        return compareTo((Pair)o);
    }

    public String toString() {
        return "{"+polynomial[0].index()+", "+polynomial[1].index()+"}, "+sugar+", "+reduction;
    }
}
