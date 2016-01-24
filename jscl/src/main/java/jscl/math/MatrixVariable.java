package jscl.math;

import javax.annotation.Nonnull;

public class MatrixVariable extends GenericVariable {
    public MatrixVariable(Generic generic) {
        super(generic);
    }

    @Nonnull
    public Variable newInstance() {
        return new MatrixVariable(null);
    }
}
