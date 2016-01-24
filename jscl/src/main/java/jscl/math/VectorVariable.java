package jscl.math;

import javax.annotation.Nonnull;

public class VectorVariable extends GenericVariable {
    public VectorVariable(Generic generic) {
        super(generic);
    }

    @Nonnull
    public Variable newInstance() {
        return new VectorVariable(null);
    }
}
