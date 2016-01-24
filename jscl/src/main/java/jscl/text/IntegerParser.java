package jscl.text;

import jscl.NumeralBase;
import jscl.math.Generic;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IntegerParser implements Parser<Integer> {

    public static final Parser<Integer> parser = new IntegerParser();

    private IntegerParser() {
    }

    public Integer parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException {
        int pos0 = p.getPosition().intValue();

        /*int n;

          ParserUtils.skipWhitespaces(expression, position);
          if (position.intValue() < expression.length() && Character.isDigit(expression.charAt(position.intValue()))) {
              char c = expression.charAt(position.intValue());
              position.increment();
              n = c - '0';
          } else {
              position.setValue(pos0);
              throw new ParseException();
          }

          while (position.intValue() < expression.length() && Character.isDigit(expression.charAt(position.intValue()))) {
              char c = expression.charAt(position.intValue());
              position.increment();
              n = 10 * n + (c - '0');
          }*/

        final NumeralBase nb = NumeralBaseParser.parser.parse(p, previousSumElement);

        final StringBuilder result = new StringBuilder();

        ParserUtils.skipWhitespaces(p);
        if (p.getPosition().intValue() < p.getExpression().length() && nb.getAcceptableCharacters().contains(p.getExpression().charAt(p.getPosition().intValue()))) {
            char c = p.getExpression().charAt(p.getPosition().intValue());
            p.getPosition().increment();
            result.append(c);
        } else {
            p.getPosition().setValue(pos0);
            throw new ParseException(Messages.msg_7, p.getPosition().intValue(), p.getExpression());
        }

        while (p.getPosition().intValue() < p.getExpression().length() && nb.getAcceptableCharacters().contains(p.getExpression().charAt(p.getPosition().intValue()))) {
            char c = p.getExpression().charAt(p.getPosition().intValue());
            p.getPosition().increment();
            result.append(c);
        }

        final String number = result.toString();
        try {
            return nb.toInteger(number);
        } catch (NumberFormatException e) {
            throw new ParseException(Messages.msg_8, p.getPosition().intValue(), p.getExpression(), number);
        }
    }
}
