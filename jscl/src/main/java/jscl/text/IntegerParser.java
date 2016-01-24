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
        int pos0 = p.position.intValue();

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
        if (p.position.intValue() < p.expression.length() && nb.getAcceptableCharacters().contains(p.expression.charAt(p.position.intValue()))) {
            char c = p.expression.charAt(p.position.intValue());
            p.position.increment();
            result.append(c);
        } else {
            p.position.setValue(pos0);
            throw new ParseException(Messages.msg_7, p.position.intValue(), p.expression);
        }

        while (p.position.intValue() < p.expression.length() && nb.getAcceptableCharacters().contains(p.expression.charAt(p.position.intValue()))) {
            char c = p.expression.charAt(p.position.intValue());
            p.position.increment();
            result.append(c);
        }

        final String number = result.toString();
        try {
            return nb.toInteger(number);
        } catch (NumberFormatException e) {
            throw new ParseException(Messages.msg_8, p.position.intValue(), p.expression, number);
        }
    }
}
