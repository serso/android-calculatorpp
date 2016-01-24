package jscl.math.function;

import org.solovyev.common.math.AbstractMathRegistry;

/**
 * User: serso
 * Date: 11/7/11
 * Time: 11:59 AM
 */
public class ConstantsRegistry extends AbstractMathRegistry<IConstant> {

    public static final String E = "e";
    public static final String C = "c";
    public static final Double C_VALUE = 299792458d;
    public static final String G = "G";
    public static final Double G_VALUE = 6.6738480E-11;
    public static final String H_REDUCED = "h";
    public static final Double H_REDUCED_VALUE = 6.6260695729E-34 / (2 * Math.PI);
    public final static String NAN = "NaN";

    public ConstantsRegistry() {
        this.add(new PiConstant());
        this.add(new ExtendedConstant(Constants.PI_INV, Math.PI, null));
        this.add(new ExtendedConstant(Constants.INF, Double.POSITIVE_INFINITY, "JsclDouble.valueOf(Double.POSITIVE_INFINITY)"));
        this.add(new ExtendedConstant(Constants.INF_2, Double.POSITIVE_INFINITY, "JsclDouble.valueOf(Double.POSITIVE_INFINITY)"));
        this.add(new ExtendedConstant(Constants.I, "√(-1)", null));
        this.add(new ExtendedConstant(new Constant(E), Math.E, null));
        this.add(new ExtendedConstant(new Constant(C), C_VALUE, null));
        this.add(new ExtendedConstant(new Constant(G), G_VALUE, null));
        this.add(new ExtendedConstant(new Constant(H_REDUCED), H_REDUCED_VALUE, null));
        this.add(new ExtendedConstant(new Constant(NAN), Double.NaN, null));
    }
}
