package jscl.text;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JsclInteger;
import jscl.math.NotIntegerException;
import jscl.math.function.Pow;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 2:45 PM
 */
class UnsignedFactor implements Parser {
    public static final Parser parser = new UnsignedFactor();

    private UnsignedFactor() {
    }

    public Object parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        final List<Generic> list = new ArrayList<Generic>();

        Generic generic = UnsignedExponent.parser.parse(p, previousSumElement);

        list.add(generic);

        while (true) {
            try {
                list.add(PowerExponentParser.parser.parse(p, null));
            } catch (ParseException e) {
                break;
            }
        }

        final ListIterator<Generic> it = list.listIterator(list.size());
        generic = it.previous();
        while (it.hasPrevious()) {
            Generic b = it.previous();
            try {
                int c = generic.integerValue().intValue();
                if (c < 0) {
                    generic = new Pow(GenericVariable.content(b, true), JsclInteger.valueOf(c)).expressionValue();
                } else {
                    generic = b.pow(c);
                }
            } catch (NotIntegerException e) {
                generic = new Pow(GenericVariable.content(b, true), GenericVariable.content(generic, true)).expressionValue();
            }
        }

        return generic;
    }
}
