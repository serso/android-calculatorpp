package jscl.math;

public class VectorVariable extends GenericVariable {
        public VectorVariable(Generic generic) {
                super(generic);
        }

        protected Variable newinstance() {
                return new VectorVariable(null);
        }
}
