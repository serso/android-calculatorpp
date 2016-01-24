package jscl.math.operator.matrix;

import jscl.math.Generic;
import jscl.math.function.FunctionsRegistry;
import jscl.math.operator.*;
import org.solovyev.common.math.AbstractMathRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 11/17/11
 * Time: 10:22 AM
 */
public class OperatorsRegistry extends AbstractMathRegistry<Operator> {

    private final static OperatorsRegistry instance = new OperatorsRegistry();

    static {
        instance.add(new Derivative(null, null, null, null));
        //instance.add(new Grad(null, null));
        //instance.add(new Divergence(null, null));
        //instance.add(new Curl(null, null));
        //instance.add(new Jacobian(null, null));
        //instance.add(new Laplacian(null, null));
        //instance.add(new Dalembertian(null, null));
        //instance.add(new Del(null, null, null));
        //instance.add(new VectorProduct(null, null));
        //instance.add(new ComplexProduct(null, null));
        //instance.add(new QuaternionProduct(null, null));
        //instance.add(new GeometricProduct(null, null, null));
        //instance.add(new MatrixProduct(null, null));
        //instance.add(new TensorProduct(null, null));
        //instance.add(new Transpose(null));
        //instance.add(new Trace(null));
        //instance.add(new Determinant(null));
        //instance.add(new Coefficient(null, null));
        //instance.add(new Solve(null, null, null));
        //instance.add(new Substitute(null, null, null));
        //instance.add(new Limit(null, null, null, null));
        instance.add(new Sum(null, null, null, null));
        instance.add(new Product(null, null, null, null));
        //instance.add(new Groebner(null, null, null, null));
        //instance.add(new Division(null, null));
        instance.add(new Modulo(null, null));
        //instance.add(new ModPow(null, null, null));
        //instance.add(new ModInverse(null, null));
        //instance.add(new EulerPhi(null));
        instance.add(new Integral(null, null, null, null));
        instance.add(new IndefiniteIntegral(null, null));
        //instance.add(new Rand());
        //instance.add(new Mean(null));
        //instance.add(new Min(null));
        //instance.add(new Max(null));
        //instance.add(new MeanSquareDeviation(null));
        //instance.add(new StandardDeviation(null));
        //instance.add(new PrimitiveRoots(null));
    }

    /*		if (operatorName.compareTo("d") == 0)
             v = new Derivative(parameters[0], parameters[1], parameters.length > 2 ? parameters[2] : parameters[1], parameters.length > 3 ? parameters[3] : JsclInteger.valueOf(1));
         else if (operatorName.compareTo("grad") == 0) v = new Grad(parameters[0], parameters[1]);
         else if (operatorName.compareTo("diverg") == 0) v = new Divergence(parameters[0], parameters[1]);
         else if (operatorName.compareTo("curl") == 0) v = new Curl(parameters[0], parameters[1]);
         else if (operatorName.compareTo("jacobian") == 0) v = new Jacobian(parameters[0], parameters[1]);
         else if (operatorName.compareTo("laplacian") == 0) v = new Laplacian(parameters[0], parameters[1]);
         else if (operatorName.compareTo("dalembertian") == 0) v = new Dalembertian(parameters[0], parameters[1]);
         else if (operatorName.compareTo("del") == 0)
             v = new Del(parameters[0], parameters[1], parameters.length > 2 ? parameters[2] : JsclInteger.valueOf(0));
         else if (operatorName.compareTo("vector") == 0) v = new VectorProduct(parameters[0], parameters[1]);
         else if (operatorName.compareTo("complex") == 0) v = new ComplexProduct(parameters[0], parameters[1]);
         else if (operatorName.compareTo("quaternion") == 0) v = new QuaternionProduct(parameters[0], parameters[1]);
         else if (operatorName.compareTo("geometric") == 0)
             v = new GeometricProduct(parameters[0], parameters[1], parameters.length > 2 ? parameters[2] : JsclInteger.valueOf(0));
         else if (operatorName.compareTo("matrix") == 0) v = new MatrixProduct(parameters[0], parameters[1]);
         else if (operatorName.compareTo("tensor") == 0) v = new TensorProduct(parameters[0], parameters[1]);
         else if (operatorName.compareTo("tran") == 0) v = new Transpose(parameters[0]);
         else if (operatorName.compareTo("trace") == 0) v = new Trace(parameters[0]);
         else if (operatorName.compareTo("det") == 0) v = new Determinant(parameters[0]);
         else if (operatorName.compareTo("coef") == 0) v = new Coefficient(parameters[0], parameters[1]);
         else if (operatorName.compareTo("solve") == 0)
             v = new Solve(parameters[0], parameters[1], parameters.length > 2 ? parameters[2] : JsclInteger.valueOf(0));
         else if (operatorName.compareTo("subst") == 0)
             v = new Substitute(parameters[0], parameters[1], parameters[2]).transmute();
         else if (operatorName.compareTo("lim") == 0)
             v = new Limit(parameters[0], parameters[1], parameters[2], parameters.length > 3 && (parameters[2].compareTo(Constant.infinity) != 0 && parameters[2].compareTo(Constant.infinity.negate()) != 0) ? JsclInteger.valueOf(parameters[3].signum()) : JsclInteger.valueOf(0));
         else if (operatorName.compareTo("sum") == 0)
             v = new Sum(parameters[0], parameters[1], parameters[2], parameters[3]);
         else if (operatorName.compareTo("prod") == 0)
             v = new Product(parameters[0], parameters[1], parameters[2], parameters[3]);
         else if (operatorName.compareTo("integral") == 0)
             v = parameters.length > 2 ? new Integral(parameters[0], parameters[1], parameters[2], parameters[3]) : new IndefiniteIntegral(parameters[0], parameters[1]);
         else if (operatorName.compareTo("groebner") == 0)
             v = new Groebner(parameters[0], parameters[1], parameters.length > 2 ? parameters[2] : Expression.valueOf("lex"), parameters.length > 3 ? parameters[3] : JsclInteger.valueOf(0)).transmute();
         else if (operatorName.compareTo("div") == 0) v = new Division(parameters[0], parameters[1]);
         else if (operatorName.compareTo("mod") == 0) v = new Modulo(parameters[0], parameters[1]);
         else if (operatorName.compareTo("modpow") == 0) v = new ModPow(parameters[0], parameters[1], parameters[2]);
         else if (operatorName.compareTo("modinv") == 0) v = new ModInverse(parameters[0], parameters[1]);
         else if (operatorName.compareTo("eulerphi") == 0) v = new EulerPhi(parameters[0]);
         else if (operatorName.compareTo("primitiveroots") == 0) v = new PrimitiveRoots(parameters[0]);*/

    @Nonnull
    public static OperatorsRegistry getInstance() {
        return instance;
    }

    @Nullable
    public Operator get(@Nonnull String name, @Nonnull Generic[] parameters) {
        final Operator operator = super.get(name);
        if (operator == null) {
            return null;
        } else {
            if (operator.getMinParameters() <= parameters.length && operator.getMaxParameters() >= parameters.length) {
                return operator.newInstance(parameters);
            } else {
                return null;
            }
        }
    }

    @Override
    public Operator get(@Nonnull String name) {
        final Operator operator = super.get(name);
        return operator == null ? null : FunctionsRegistry.copy(operator);
    }
}
