package jscl.math;

import jscl.mathml.MathML;

import javax.annotation.Nonnull;

class IntegerVariable extends GenericVariable {
    IntegerVariable(Generic generic) {
        super(generic);
    }

    public Generic substitute(Variable variable, Generic generic) {
        if (isIdentity(variable)) return generic;
        else return content.substitute(variable, generic);
    }

    public Generic elementary() {
        return content.elementary();
    }

    public Generic simplify() {
        return content.simplify();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(").append(content).append(")");
        return buffer.toString();
    }

    public String toJava() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(").append(content.toJava()).append(")");
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        int exponent = data instanceof Integer ? ((Integer) data).intValue() : 1;
        if (exponent == 1) bodyToMathML(element);
        else {
            MathML e1 = element.element("msup");
            bodyToMathML(e1);
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    void bodyToMathML(MathML element) {
        MathML e1 = element.element("mfenced");
        content.toMathML(e1, null);
        element.appendChild(e1);
    }

    @Nonnull
    public Variable newInstance() {
        return new IntegerVariable(null);
    }
}
