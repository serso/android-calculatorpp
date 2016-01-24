package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NotIntegerException;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 12/23/11
 * Time: 4:47 PM
 */
public class Gcd extends Operator {

    public Gcd(@Nonnull Generic first, @Nonnull Generic second) {
        this(new Generic[]{first, second});
    }

    public Gcd() {
        this(new Generic[2]);
    }

    private Gcd(@Nonnull Generic[] parameters) {
        super("gcd", parameters);
    }


    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Gcd(parameters);
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    @Override
    public Generic selfExpand() {
        return expressionValue();
    }

    @Override
    public Generic numeric() {
        final Generic first = parameters[0];
        final Generic second = parameters[1];

        try {
            final JsclInteger firstInt = first.integerValue();
            final JsclInteger secondInt = second.integerValue();

            return firstInt.gcd(secondInt);
        } catch (NotIntegerException e) {
            // ok => continue
        }

        return first.gcd(second);
    }

    @Nonnull
    @Override
    public Gcd newInstance() {
        return new Gcd();
    }
}
