package jscl.util;

import javax.annotation.Nonnull;

public class ExpressionGenerator extends AbstractExpressionGenerator<String> {

    public ExpressionGenerator() {
        super();
    }

    public ExpressionGenerator(int depth) {
        super(depth);
    }

    public static void main(String... args) {
        System.out.println(new ExpressionGenerator(20).generate());
    }

    @Nonnull
    @Override
    public String generate() {
        StringBuilder result = new StringBuilder();

        result.append(generateNumber());

        int i = 0;
        while (i < getDepth()) {

            final Operation operation = generateOperation();
            final Function function = generateFunction();
            final boolean brackets = generateBrackets();

            result.append(operation.getToken());

            if (function == null) {
                result.append(generateNumber());
            } else {
                result.append(function.getToken()).append("(").append(generateNumber()).append(")");
            }

            if (brackets) {
                result = new StringBuilder("(").append(result).append(")");
            }

            i++;
        }

        return result.toString();
    }

}
