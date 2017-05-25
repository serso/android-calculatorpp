package jscl.math.function;

import org.solovyev.common.math.AbstractMathRegistry;
import org.solovyev.common.math.MathRegistry;

public class ConstantsRegistry extends AbstractMathRegistry<IConstant> {
    private static final ConstantsRegistry INSTANCE = new ConstantsRegistry();

    public static final String E = "e";
    public static final String C = "c";
    public static final Double C_VALUE = 299792458d;
    public static final String G = "G";
    public static final Double G_VALUE = 6.6738480E-11;
    public static final String H_REDUCED = "h";
    public static final Double H_REDUCED_VALUE = 6.6260695729E-34 / (2 * Math.PI);
    public final static String NAN = "NaN";

    public ConstantsRegistry() {
    }

    @Override
    public void onInit() {
        add(new PiConstant());
        add(new ExtendedConstant(Constants.PI_INV, Math.PI, null));
        add(new ExtendedConstant(Constants.INF, Double.POSITIVE_INFINITY, "JsclDouble.valueOf(Double.POSITIVE_INFINITY)"));
        add(new ExtendedConstant(Constants.INF_2, Double.POSITIVE_INFINITY, "JsclDouble.valueOf(Double.POSITIVE_INFINITY)"));
        add(new ExtendedConstant(Constants.I, "√(-1)", null));
        add(new ExtendedConstant(new Constant(E), Math.E, null));
        add(new ExtendedConstant(new Constant(C), C_VALUE, null));
        add(new ExtendedConstant(new Constant(G), G_VALUE, null));
        add(new ExtendedConstant(new Constant(H_REDUCED), H_REDUCED_VALUE, null));
        add(new ExtendedConstant(new Constant(NAN), Double.NaN, null));
    }

    public static MathRegistry<IConstant> getInstance() {
        INSTANCE.init();
        return INSTANCE;
    }

    public static MathRegistry<IConstant> lazyInstance() {
        return INSTANCE;
    }
}
