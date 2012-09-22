package org.solovyev.android.calculator;

import jscl.MathEngine;
import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;
import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 12:43
 */
public interface CalculatorEngine {

    @NotNull
    String getMultiplicationSign();

    @NotNull
    CalculatorMathRegistry<IConstant> getVarsRegistry();

    @NotNull
    CalculatorMathRegistry<Function> getFunctionsRegistry();

    @NotNull
    CalculatorMathRegistry<Operator> getOperatorsRegistry();

    @NotNull
    CalculatorMathRegistry<Operator> getPostfixFunctionsRegistry();

    @NotNull
    MathEngine getEngine();

    void init();

    void reset();

    void softReset();
}
