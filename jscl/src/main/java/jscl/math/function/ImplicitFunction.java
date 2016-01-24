package jscl.math.function;

import jscl.math.Generic;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.mathml.MathML;
import jscl.util.ArrayComparator;

import javax.annotation.Nonnull;

public class ImplicitFunction extends Function {

    private int derivations[];

    private Generic subscripts[];

    public ImplicitFunction(String name, Generic parameter[], int derivations[], Generic subscripts[]) {
        super(name, parameter);
        this.derivations = derivations;
        this.subscripts = subscripts;
    }

    static int compareDerivation(int c1[], int c2[]) {
        int n = c1.length;
        for (int i = n - 1; i >= 0; i--) {
            if (c1[i] < c2[i]) return -1;
            else if (c1[i] > c2[i]) return 1;
        }
        return 0;
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        int c[] = new int[derivations.length];
        for (int i = 0; i < c.length; i++) {
            if (i == n) {
                if (derivations[i] > 0) c[i] = derivations[i] - 1;
                else throw new NotIntegrableException(this);
            } else c[i] = derivations[i];
        }
        return new ImplicitFunction(name, parameters, c, subscripts).selfExpand();
    }

    public Generic derivative(int n) {
        int c[] = new int[derivations.length];
        for (int i = 0; i < c.length; i++) {
            if (i == n) c[i] = derivations[i] + 1;
            else c[i] = derivations[i];
        }
        return new ImplicitFunction(name, parameters, c, subscripts).selfExpand();
    }

    public Generic selfExpand() {
        return expressionValue();
    }

    public Generic selfElementary() {
        return expressionValue();
    }

    public Generic selfSimplify() {
        return expressionValue();
    }

    /*@Override
     public Generic numeric() {
         return evaluateNumerically();
     }*/

    public Generic selfNumeric() {
        throw new ArithmeticException();
        /*Function function = FunctionsRegistry.getInstance().get(this.name);

          Generic result;
          if ( function == null ) {
              throw new ArithmeticException();
          } else {
              function.setParameters(this.parameters);

              result = function;
              for (int derivation : derivations) {
                  for ( int i = 0; i < derivation; i++ ) {
                      result = result.derivative(derivation);
                  }
              }

              result = result.numeric();
          }

          return result;*/
    }

    public int compareTo(Variable that) {
        if (this == that) return 0;
        int c = comparator.compare(this, that);
        if (c < 0) return -1;
        else if (c > 0) return 1;
        else {
            ImplicitFunction v = (ImplicitFunction) that;
            c = name.compareTo(v.name);
            if (c < 0) return -1;
            else if (c > 0) return 1;
            else {
                c = ArrayComparator.comparator.compare(subscripts, v.subscripts);
                if (c < 0) return -1;
                else if (c > 0) return 1;
                else {
                    c = compareDerivation(derivations, v.derivations);
                    if (c < 0) return -1;
                    else if (c > 0) return 1;
                    else return ArrayComparator.comparator.compare(parameters, v.parameters);
                }
            }
        }
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();

        int n = 0;
        for (int derivation : derivations) {
            n += derivation;
        }

        result.append(name);

        for (Generic aSubscript : subscripts) {
            result.append("[").append(aSubscript).append("]");
        }

        if (n == 0) {
            // do nothing
        } else if (parameters.length == 1 && n <= Constant.PRIME_CHARS) {
            result.append(Constant.primeChars(n));
        } else {
            result.append(derivationToString());
        }

        result.append("(");

        for (int i = 0; i < parameters.length; i++) {
            result.append(parameters[i]).append(i < parameters.length - 1 ? ", " : "");
        }

        result.append(")");

        return result.toString();
    }

    String derivationToString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{");
        for (int i = 0; i < derivations.length; i++) {
            buffer.append(derivations[i]).append(i < derivations.length - 1 ? ", " : "");
        }
        buffer.append("}");
        return buffer.toString();
    }

    public String toJava() {
        final StringBuilder result = new StringBuilder();

        int n = 0;
        for (int derivation : derivations) {
            n += derivation;
        }

        result.append(name);
        if (n == 0) {
            // do nothing
        } else if (parameters.length == 1 && n <= Constant.PRIME_CHARS) {
            result.append(Constant.underscores(n));
        } else {
            result.append(derivationToJava());
        }

        result.append("(");
        for (int i = 0; i < parameters.length; i++) {
            result.append(parameters[i].toJava()).append(i < parameters.length - 1 ? ", " : "");
        }
        result.append(")");

        for (Generic subscript : subscripts) {
            result.append("[").append(subscript.integerValue().intValue()).append("]");
        }

        return result.toString();
    }

    String derivationToJava() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < derivations.length; i++) {
            buffer.append("_").append(derivations[i]);
        }
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        MathML e1;
        int exponent = data instanceof Integer ? (Integer) data : 1;
        if (exponent == 1) bodyToMathML(element);
        else {
            e1 = element.element("msup");
            bodyToMathML(e1);
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
        e1 = element.element("mfenced");
        for (Generic parameter : parameters) {
            parameter.toMathML(e1, null);
        }
        element.appendChild(e1);
    }

    void bodyToMathML(MathML element) {
        int n = 0;
        for (int derivation : derivations) {
            n += derivation;
        }

        if (subscripts.length == 0) {
            if (n == 0) {
                nameToMathML(element);
            } else {
                MathML e1 = element.element("msup");
                nameToMathML(e1);
                derivationToMathML(e1, n);
                element.appendChild(e1);
            }
        } else {
            if (n == 0) {
                MathML e1 = element.element("msub");
                nameToMathML(e1);
                MathML e2 = element.element("mrow");
                for (Generic subscript : subscripts) {
                    subscript.toMathML(e2, null);
                }
                e1.appendChild(e2);
                element.appendChild(e1);
            } else {
                MathML e1 = element.element("msubsup");
                nameToMathML(e1);
                MathML e2 = element.element("mrow");
                for (Generic subscript : subscripts) {
                    subscript.toMathML(e2, null);
                }
                e1.appendChild(e2);
                derivationToMathML(e1, n);
                element.appendChild(e1);
            }
        }
    }

    void derivationToMathML(MathML element, int n) {
        if (parameters.length == 1 && n <= Constant.PRIME_CHARS) {
            Constant.primeCharsToMathML(element, n);
        } else {
            MathML e1 = element.element("mfenced");
            for (int derivation : derivations) {
                MathML e2 = element.element("mn");
                e2.appendChild(element.text(String.valueOf(derivation)));
                e1.appendChild(e2);
            }
            element.appendChild(e1);
        }
    }

    @Nonnull
    public Variable newInstance() {
        return new ImplicitFunction(name, new Generic[parameters.length], derivations, subscripts);
    }
}
