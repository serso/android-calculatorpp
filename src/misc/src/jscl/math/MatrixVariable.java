package jscl.math;

public class MatrixVariable extends GenericVariable {
        public MatrixVariable(Generic generic) {
                super(generic);
        }

        protected Variable newinstance() {
                return new MatrixVariable(null);
        }
}
