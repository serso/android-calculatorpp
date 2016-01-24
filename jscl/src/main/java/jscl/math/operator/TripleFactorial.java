package jscl.math.operator;

import jscl.NotSupportedException;
import jscl.math.Generic;
import jscl.math.Variable;
import jscl.mathml.MathML;
import jscl.text.ParserUtils;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 12/15/11
 * Time: 10:43 PM
 */
public class TripleFactorial extends PostfixFunction {

    public static final String NAME = "!!!";

    public TripleFactorial(Generic expression) {
        super(NAME, new Generic[]{expression});
    }

    private TripleFactorial(Generic[] parameter) {
        super(NAME, ParserUtils.copyOf(parameter, 1));
    }

    @Override
    public int getMinParameters() {
        return 1;
    }

    public Generic selfExpand() {
        throw new NotSupportedException(Messages.msg_18);
    }

    @Override
    public Generic selfNumeric() {
        throw new NotSupportedException(Messages.msg_18);
    }

    public void toMathML(MathML element, Object data) {
        throw new NotSupportedException(Messages.msg_18);
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new TripleFactorial(parameters);
    }

    @Override
    public String toString() {
        throw new NotSupportedException(Messages.msg_18);
    }

    @Nonnull
    public Variable newInstance() {
        return new TripleFactorial((Generic) null);
    }
}
