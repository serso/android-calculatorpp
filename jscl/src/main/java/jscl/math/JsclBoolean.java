package jscl.math;

public class JsclBoolean extends ModularInteger {
    public static final JsclBoolean factory = new JsclBoolean(0);
    private static final JsclBoolean zero = factory;
    private static final JsclBoolean one = new JsclBoolean(1);

    public JsclBoolean(long content) {
        super(content, 2);
    }

    protected ModularInteger newinstance(long content) {
        return content % 2 == 0 ? zero : one;
    }
}
