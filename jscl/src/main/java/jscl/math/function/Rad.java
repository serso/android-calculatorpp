package jscl.math.function;

import jscl.AngleUnit;
import jscl.math.Generic;
import jscl.math.Variable;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 11/14/11
 * Time: 1:40 PM
 */
public class Rad extends AbstractDms {

    public Rad(Generic degrees, Generic minutes, Generic seconds) {
        super("rad", degrees, minutes, seconds);
    }

    @Override
    public Generic selfNumeric() {
        return AngleUnit.deg.transform(AngleUnit.rad, super.selfNumeric());
    }

    @Nonnull
    @Override
    public Variable newInstance() {
        return new Rad(null, null, null);
    }
}
