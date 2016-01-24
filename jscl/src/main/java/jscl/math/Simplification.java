package jscl.math;

import jscl.math.function.*;
import jscl.math.polynomial.Basis;
import jscl.math.polynomial.Monomial;
import jscl.math.polynomial.Polynomial;
import jscl.math.polynomial.UnivariatePolynomial;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class Simplification {

    private final Map<Variable, Generic> cache = new TreeMap<Variable, Generic>();
    private final List<Constraint> constraints = new ArrayList<Constraint>();
    Generic result;
    boolean linear;

    private Simplification() {
    }

    public static Generic compute(@Nonnull Generic generic) {
        final Simplification s = new Simplification();
        s.computeValue(generic);
        return s.getValue();
    }

    void computeValue(Generic generic) {
        Debug.println("simplification");
        Debug.increment();

        final Variable t = new TechnicalVariable("t");
        linear = false;
        process(new Constraint(t, t.expressionValue().subtract(generic), false));
        UnivariatePolynomial p = polynomial(t);

        switch (p.degree()) {
            case 0:
                result = generic;
                break;
            case 1:
                result = new Root(p, 0).selfSimplify();
                break;
//          case 2:
//              int n=branch(generic,p);
//              if(n<p.degree()) linear(new Root(p,n).expressionValue());
//              else linear(generic);
//              break;
            default:
                linear(generic);
        }

        Debug.decrement();
    }

    void linear(Generic generic) {
        Variable t = new TechnicalVariable("t");
        linear = true;
        constraints.clear();
        process(new Constraint(t, t.expressionValue().subtract(generic), false));
        UnivariatePolynomial p = polynomial(t);
        switch (p.degree()) {
            case 0:
                result = generic;
                break;
            default:
                result = new Root(p, 0).selfSimplify();
        }
    }

    int branch(Generic generic, UnivariatePolynomial polynomial) {
        int n = polynomial.degree();
        Variable t = new TechnicalVariable("t");
        linear = true;
        for (int i = 0; i < n; i++) {
            constraints.clear();
            process(new Constraint(t, t.expressionValue().subtract(generic.subtract(new Root(polynomial, i).expressionValue())), false));
            Generic a = polynomial(t).solve();
            if (a != null && a.signum() == 0) {
                return i;
            }
        }
        return n;
    }

    UnivariatePolynomial polynomial(Variable t) {
        Polynomial fact = Polynomial.factory(t);
        int n = constraints.size();
        Generic a[] = new Generic[n];
        Variable unk[] = new Variable[n];
        if (linear) {
            int j = 0;
            for (Constraint constraint : constraints) {
                if (constraint.reduce) {
                    a[j] = constraint.generic;
                    unk[j] = constraint.unknown;
                    j++;
                }
            }
            int k = 0;
            for (Constraint c : constraints) {
                if (!c.reduce) {
                    a[j] = c.generic;
                    unk[j] = c.unknown;
                    j++;
                    k++;
                }
            }
            a = solve(a, unk, k);
            for (Generic anA : a) {
                UnivariatePolynomial p = (UnivariatePolynomial) fact.valueOf(anA);
                if (p.degree() == 1) return p;
            }
            return null;
        } else {
            for (int i = 0; i < n; i++) {
                Constraint c = constraints.get(i);
                a[i] = c.generic;
                unk[i] = c.unknown;
            }
            a = solve(a, unk, n);
            return (UnivariatePolynomial) fact.valueOf(a[0]);
        }
    }

    Generic[] solve(Generic generic[], Variable unknown[], int n) {
        Variable unk[] = Basis.augmentUnknown(unknown, generic);
        return Basis.compute(generic, unk, Monomial.kthElimination(n)).elements();
    }

    void process(@Nonnull Constraint c) {

        constraints.add(c);

        int n1 = 0;
        int n2 = 0;
        do {
            n1 = n2;
            n2 = constraints.size();
            for (int i = n1; i < n2; i++) {
                subProcess(constraints.get(i));
            }
        } while (n1 < n2);
    }

    void subProcess(@Nonnull Constraint c) {
        for (Variable v : c.generic.variables()) {
            if (constraints.contains(new Constraint(v))) {
                continue;
            }

            Constraint result = null;

            if (v instanceof Fraction) {
                final Generic parameters[] = ((Fraction) v).getParameters();
                result = new Constraint(v, v.expressionValue().multiply(parameters[1]).subtract(parameters[0]), false);
            } else if (v instanceof Sqrt) {

                if (linear) {
                    result = linearConstraint(v);
                }

                if (result == null) {
                    final Generic parameters[] = ((Sqrt) v).getParameters();
                    result = new Constraint(v, v.expressionValue().pow(2).subtract(parameters[0]), true);
                }

            } else if (v instanceof Cubic) {

                if (linear) {
                    result = linearConstraint(v);
                }

                if (result == null) {
                    final Generic parameters[] = ((Cubic) v).getParameters();
                    result = new Constraint(v, v.expressionValue().pow(3).subtract(parameters[0]), true);
                }
            } else if (v instanceof Pow) {

                try {
                    Root r = ((Pow) v).rootValue();
                    int d = r.degree();

                    if (linear) {
                        result = linearConstraint(v);
                    }

                    if (result == null) {
                        final Generic parameters[] = r.getParameters();
                        result = new Constraint(v, v.expressionValue().pow(d).subtract(parameters[0].negate()), d > 1);
                    }

                } catch (NotRootException e) {
                    result = linearConstraint(v);
                }
            } else if (v instanceof Root) {
                try {
                    Root r = (Root) v;
                    int d = r.degree();
                    int n = r.subscript().integerValue().intValue();

                    if (linear) {
                        result = linearConstraint(v);
                    }

                    if (result == null) {
                        final Generic parameters[] = r.getParameters();
                        result = new Constraint(v, Root.sigma(parameters, d - n).multiply(JsclInteger.valueOf(-1).pow(d - n)).multiply(parameters[d]).subtract(parameters[n]), d > 1);
                    }
                } catch (NotIntegerException e) {
                    result = linearConstraint(v);
                }
            } else {
                result = linearConstraint(v);
            }

            if (result != null) {
                constraints.add(result);
            }
        }
    }

    @Nullable
    private Constraint linearConstraint(@Nonnull Variable v) {
        Generic s = cache.get(v);
        if (s == null) {
            s = v.simplify();
            cache.put(v, s);
        }

        Generic a = v.expressionValue().subtract(s);
        if (a.signum() != 0) {
            return new Constraint(v, a, false);
        } else {
            return null;
        }
    }

    Generic getValue() {
        return result;
    }
}

class Constraint {
    Variable unknown;
    Generic generic;
    boolean reduce;

    Constraint(Variable unknown, Generic generic, boolean reduce) {
        this.unknown = unknown;
        this.generic = generic;
        this.reduce = reduce;
    }

    Constraint(Variable unknown) {
        this(unknown, null, false);
    }

    public boolean equals(Object obj) {
        return unknown.compareTo(((Constraint) obj).unknown) == 0;
    }
}
