package jscl.text;

import jscl.math.Generic;
import jscl.math.operator.Operator;
import jscl.math.operator.matrix.OperatorsRegistry;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OperatorParser implements Parser<Operator> {

    public static final Parser<Operator> parser = new OperatorParser();

    private OperatorParser() {
    }

    static boolean valid(@Nullable String name) {
        return name != null && OperatorsRegistry.getInstance().getNames().contains(name);
    }

    @Nonnull
    public Operator parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.getPosition().intValue();

        final String operatorName = Identifier.parser.parse(p, previousSumElement);
        if (!valid(operatorName)) {
            ParserUtils.throwParseException(p, pos0, Messages.msg_3, operatorName);
        }

        final Operator operator = OperatorsRegistry.getInstance().get(operatorName);

        Operator result = null;
        if (operator != null) {
            final Generic parameters[] = ParserUtils.parseWithRollback(new ParameterListParser(operator.getMinParameters()), pos0, previousSumElement, p);

            result = OperatorsRegistry.getInstance().get(operatorName, parameters);
            if (result == null) {
                ParserUtils.throwParseException(p, pos0, Messages.msg_2, operatorName);
            }
        } else {
            ParserUtils.throwParseException(p, pos0, Messages.msg_3, operatorName);
        }

        assert result != null;
        return result;
    }

}
