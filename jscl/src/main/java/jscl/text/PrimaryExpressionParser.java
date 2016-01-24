package jscl.text;

import jscl.math.ExpressionVariable;
import jscl.math.Generic;
import jscl.math.Variable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 2:45 PM
 */
public class PrimaryExpressionParser implements Parser<Generic> {

    public static final Parser<Generic> parser = new PrimaryExpressionParser();

    private static final List<Parser<? extends Generic>> parsers = Arrays.asList(
            new VariableConverter<Variable>(DoubleVariableParser.parser),
            JsclIntegerParser.parser,
            new VariableConverter<Variable>(VariableParser.parser),
            new VariableConverter<Variable>(MatrixVariableParser.parser),
            new VariableConverter<Variable>(VectorVariableParser.parser),
            new VariableConverter<ExpressionVariable>(BracketedExpression.parser));

    private static final Parser<Generic> internalParser = new MultiTryParser<Generic>(new ArrayList<Parser<? extends Generic>>(parsers));

    private PrimaryExpressionParser() {
    }

    public Generic parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        return internalParser.parse(p, previousSumElement);
    }
}
