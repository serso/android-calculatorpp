package jscl.text;

import jscl.math.Generic;
import jscl.math.function.Function;
import jscl.math.function.FunctionsRegistry;
import jscl.math.function.ImplicitFunction;
import jscl.math.operator.matrix.OperatorsRegistry;
import jscl.text.msg.Messages;
import jscl.util.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImplicitFunctionParser implements Parser<Function> {
    public static final Parser<Function> parser = new ImplicitFunctionParser();

    private ImplicitFunctionParser() {
    }

    public Function parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();
        Generic a[];

        final String name = ParserUtils.parseWithRollback(CompoundIdentifier.parser, pos0, previousSumElement, p);
        if (FunctionsRegistry.getInstance().getNames().contains(name) || OperatorsRegistry.getInstance().getNames().contains(name)) {
            p.position.setValue(pos0);
            throw p.exceptionsPool.obtain(p.position.intValue(), p.expression, Messages.msg_6, Collections.singletonList(name));
        }

        final List<Generic> subscripts = new ArrayList<Generic>();
        while (true) {
            try {
                subscripts.add(Subscript.parser.parse(p, previousSumElement));
            } catch (ParseException e) {
                p.exceptionsPool.release(e);
                break;
            }
        }

        int b[];
        try {
            b = Derivation.parser.parse(p, previousSumElement);
        } catch (ParseException e) {
            p.exceptionsPool.release(e);
            b = new int[0];
        }
        try {
            a = ParameterListParser.parser1.parse(p, previousSumElement);
        } catch (ParseException e) {
            p.position.setValue(pos0);
            throw e;
        }

        int derivations[] = new int[a.length];
        for (int i = 0; i < a.length && i < b.length; i++) {
            derivations[i] = b[i];
        }

        return new ImplicitFunction(name, a, derivations, ArrayUtils.toArray(subscripts, new Generic[subscripts.size()]));
    }
}

class Derivation implements Parser<int[]> {

    public static final Parser<int[]> parser = new Derivation();

    private Derivation() {
    }

    public int[] parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        try {
            return new int[]{PrimeCharacters.parser.parse(p, previousSumElement)};
        } catch (ParseException e) {
            p.exceptionsPool.release(e);
        }
        return SuperscriptList.parser.parse(p, previousSumElement);
    }
}

class SuperscriptList implements Parser<int[]> {

    public static final Parser<int[]> parser = new SuperscriptList();

    private SuperscriptList() {
    }

    public int[] parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        ParserUtils.tryToParse(p, pos0, '{');

        final List<Integer> result = new ArrayList<Integer>();
        try {
            result.add(IntegerParser.parser.parse(p, previousSumElement));
        } catch (ParseException e) {
            p.position.setValue(pos0);
            throw e;
        }

        while (true) {
            try {
                result.add(CommaAndInteger.parser.parse(p, previousSumElement));
            } catch (ParseException e) {
                p.exceptionsPool.release(e);
                break;
            }
        }

        ParserUtils.tryToParse(p, pos0, '}');

        ParserUtils.skipWhitespaces(p);

        return ArrayUtils.toArray(result, new int[result.size()]);
    }
}

class CommaAndInteger implements Parser<Integer> {

    public static final Parser<Integer> parser = new CommaAndInteger();

    private CommaAndInteger() {
    }

    public Integer parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        ParserUtils.skipWhitespaces(p);

        return ParserUtils.parseWithRollback(IntegerParser.parser, pos0, previousSumElement, p);
    }
}
