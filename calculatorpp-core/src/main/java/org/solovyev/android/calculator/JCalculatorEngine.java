package org.solovyev.android.calculator;

import jscl.MathEngine;
import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.math.MathRegistry;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 12:43
 */
public interface JCalculatorEngine {

    @NotNull
    String getMultiplicationSign();

    @NotNull
    MathRegistry<IConstant> getVarsRegistry();

    @NotNull
    MathRegistry<Function> getFunctionsRegistry();

    @NotNull
    MathRegistry<Operator> getOperatorsRegistry();

    @NotNull
    MathRegistry<Operator> getPostfixFunctionsRegistry();

    @NotNull
    MathEngine getEngine();
}
