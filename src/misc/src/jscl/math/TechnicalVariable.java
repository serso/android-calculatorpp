package jscl.math;

public class TechnicalVariable extends Variable {
        public int subscript[];

        public TechnicalVariable(String name) {
                this(name,new int[0]);
        }

        public TechnicalVariable(String name, int subscript[]) {
                super(name);
                this.subscript=subscript;
        }

        public Generic antiderivative(Variable variable) throws NotIntegrableException {
                throw new NotIntegrableException();
        }

        public Generic derivative(Variable variable) {
                if(isIdentity(variable)) return JSCLInteger.valueOf(1);
                else return JSCLInteger.valueOf(0);
        }

        public Generic substitute(Variable variable, Generic generic) {
                if(isIdentity(variable)) return generic;
                else return expressionValue();
        }

        public Generic expand() {
                return expressionValue();
        }

        public Generic factorize() {
                return expressionValue();
        }

        public Generic elementary() {
                return expressionValue();
        }

        public Generic simplify() {
                return expressionValue();
        }

        public Generic numeric() {
                throw new ArithmeticException();
        }

        public boolean isConstant(Variable variable) {
                return !isIdentity(variable);
        }

        public int compareTo(Variable variable) {
                if(this==variable) return 0;
                int c=comparator.compare(this,variable);
                if(c<0) return -1;
                else if(c>0) return 1;
                else {
                        TechnicalVariable v=(TechnicalVariable)variable;
                        c=name.compareTo(v.name);
                        if(c<0) return -1;
                        else if(c>0) return 1;
                        else return compareSubscript(subscript,v.subscript);
                }
        }

        public int compareSubscript(int c1[], int c2[]) {
                if(c1.length<c2.length) return -1;
                else if(c1.length>c2.length) return 1;
                for(int i=0;i<c1.length;i++) {
                        if(c1[i]<c2[i]) return -1;
                        else if(c1[i]>c2[i]) return 1;
                }
                return 0;
        }

        public String toString() {
                StringBuffer buffer=new StringBuffer();
                buffer.append(name);
                if(subscript.length==1) buffer.append(subscript[0]);
                else for(int i=0;i<subscript.length;i++) buffer.append("[").append(subscript[i]).append("]");
                return buffer.toString();
        }

        public String toJava() {
                return null;
        }

        public String toMathML(Object data) {
                return null;
        }

        protected Variable newinstance() {
                return new TechnicalVariable(name);
        }
}
