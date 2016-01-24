package jscl.math.polynomial;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.Variable;
import jscl.math.polynomial.groebner.Standard;
import jscl.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class Basis {
    public static final int DATA_STRUCT = 0x3;
    public static final int ARRAY_DECLINED = 0x0;
    public static final int ARRAY = 0x1;
    public static final int TREE = 0x2;
    public static final int LIST = 0x3;
    public static final int DEGREE = 0x4;
    public static final int DEFINING_EQS = 0x8;
    public static final int POWER_SIZE = 0x30;
    public static final int POWER_32 = 0x00;
    public static final int POWER_8 = 0x10;
    public static final int POWER_2 = 0x20;
    public static final int POWER_2_DEFINED = 0x30;
    public static final int GEO_BUCKETS = 0x40;
    public static final int ALGORITHM = 0x180;
    public static final int BUCHBERGER = 0x000;
    public static final int F4 = 0x080;
    public static final int BLOCK = 0x100;
    public static final int INSTRUMENTED = 0x200;
    public static final int GM_SETTING = 0x400;
    public static final int SUGAR = 0x800;
    public static final int FUSSY = 0x1000;
    public static final int F4_SIMPLIFY = 0x2000;
    static final int DEFAULT = GM_SETTING | SUGAR;
    final Polynomial factory;
    final Generic element[];

    public Basis(Generic element[], Polynomial factory) {
        this.element = element;
        this.factory = factory;
    }

    public static Basis compute(Generic generic[], Variable unknown[]) {
        return compute(generic, unknown, Monomial.lexicographic);
    }

    public static Basis compute(Generic generic[], Variable unknown[], Ordering ordering) {
        return compute(generic, unknown, ordering, 0);
    }

    public static Basis compute(Generic generic[], Variable unknown[], Ordering ordering, int modulo) {
        return compute(generic, unknown, ordering, modulo, 0);
    }

    public static Basis compute(Generic generic[], Variable unknown[], Ordering ordering, int modulo, int flags) {
        flags ^= DEFAULT;
        return compute(generic, unknown, ordering, modulo, flags, (flags & Basis.DEGREE) > 0, (flags & DEFINING_EQS) > 0);
    }

    static Basis compute(Generic generic[], Variable unknown[], Ordering ordering, int modulo, int flags, boolean degree, boolean defining) {
        if (degree)
            return compute(compute(generic, unknown, Monomial.degreeReverseLexicographic, modulo, flags, false, defining).elements(), unknown, ordering, modulo, flags, false, defining);
        return Standard.compute(new Basis(defining ? augment(defining(unknown, modulo), generic) : generic, Polynomial.factory(unknown, ordering, modulo, flags)), flags);
    }

    public static Generic[] defining(Variable unknown[], int modulo) {
        Generic a[] = new Generic[unknown.length];
        for (int i = 0; i < unknown.length; i++) {
            Generic s = unknown[i].expressionValue();
            a[i] = s.subtract(s.pow(modulo));
        }
        return a;
    }

    public static boolean compatible(Generic generic[]) {
        return !(generic.length > 0 && generic[0].compareTo(JsclInteger.valueOf(1)) == 0);
    }

    public static Generic[] augment(Generic element[], Generic generic[]) {
        return (Generic[]) ArrayUtils.concat(element, generic, new Generic[element.length + generic.length]);
    }

    public static Variable[] augmentUnknown(Variable unknown[], Generic generic[]) {
        Variable va[] = Expression.variables(generic);
        List l = new ArrayList();
        for (Variable anUnknown : unknown) l.add(anUnknown);
        int n = 0;
        for (int i = 0; i < va.length; i++) {
            Variable v = va[i];
            if (!l.contains(v)) {
                l.add(n++, v);
            }
        }
        return (Variable[]) ArrayUtils.toArray(l, new Variable[l.size()]);
    }

    public Basis valueof(Generic generic[]) {
        return new Basis(generic, factory);
    }

    public Basis modulo(int modulo) {
        return new Basis(element, Polynomial.factory(factory, modulo));
    }

    public Generic[] elements() {
        return element;
    }

    public Ordering ordering() {
        return factory.ordering();
    }

    public Polynomial polynomial(Generic generic) {
        return factory.valueOf(generic).normalize().freeze();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        for (int i = 0; i < element.length; i++) {
            buffer.append(polynomial(element[i])).append(i < element.length - 1 ? ", " : "");
        }
        buffer.append("}");
        buffer.append(", " + ArrayUtils.toString(factory.monomialFactory.unknown()));
        return buffer.toString();
    }
}
