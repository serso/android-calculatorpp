package jscl.math;

public class JSCLBoolean extends ModularInteger {
    public static final JSCLBoolean factory=new JSCLBoolean(0);

    public JSCLBoolean(long content) {
        super(content,2);
    }

    protected ModularInteger newinstance(long content) {
        return content%2==0?zero:one;
    }

    private static final JSCLBoolean zero=factory;
    private static final JSCLBoolean one=new JSCLBoolean(1);
}
