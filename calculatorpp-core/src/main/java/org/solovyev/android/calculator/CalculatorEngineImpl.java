package org.solovyev.android.calculator;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.Generic;
import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;
import jscl.text.ParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormatSymbols;

/**
 * User: serso
 * Date: 9/23/12
 * Time: 5:34 PM
 */
public class CalculatorEngineImpl implements CalculatorEngine {

    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */

    private static final String MULTIPLICATION_SIGN_DEFAULT = "×";

    private static final String MAX_CALCULATION_TIME_DEFAULT = "5";

    /*
    **********************************************************************
    *
    *                           ENGINE/REGISTRIES
    *
    **********************************************************************
    */
    @NotNull
    private final MathEngine engine;

    @NotNull
    private final CalculatorMathEngine mathEngine;

    @NotNull
    private final CalculatorMathRegistry<IConstant> varsRegistry;

    @NotNull
    private final CalculatorMathRegistry<Function> functionsRegistry;

    @NotNull
    private final CalculatorMathRegistry<Operator> operatorsRegistry;

    @NotNull
    private final CalculatorMathRegistry<Operator> postfixFunctionsRegistry;

    @NotNull
    private final Object lock;

    /*
    **********************************************************************
    *
    *                           PREFERENCES
    *
    **********************************************************************
    */


    private int timeout = Integer.valueOf(MAX_CALCULATION_TIME_DEFAULT);

    @NotNull
    private String multiplicationSign = MULTIPLICATION_SIGN_DEFAULT;

    public CalculatorEngineImpl(@NotNull JsclMathEngine engine,
                                @NotNull CalculatorMathRegistry<IConstant> varsRegistry,
                                @NotNull CalculatorMathRegistry<Function> functionsRegistry,
                                @NotNull CalculatorMathRegistry<Operator> operatorsRegistry,
                                @NotNull CalculatorMathRegistry<Operator> postfixFunctionsRegistry,
                                @Nullable Object lock) {

        this.engine = engine;
        this.mathEngine = new JsclCalculatorMathEngine(engine);

        this.engine.setRoundResult(true);
        this.engine.setUseGroupingSeparator(true);

        this.varsRegistry = varsRegistry;
        this.functionsRegistry = functionsRegistry;
        this.operatorsRegistry = operatorsRegistry;
        this.postfixFunctionsRegistry = postfixFunctionsRegistry;
        this.lock = lock == null ? new Object() : lock;
    }

    /*
    **********************************************************************
    *
    *                           REGISTRIES
    *
    **********************************************************************
    */
    @NotNull
    @Override
    public CalculatorMathRegistry<IConstant> getVarsRegistry() {
        return this.varsRegistry;
    }

    @NotNull
    @Override
    public CalculatorMathRegistry<Function> getFunctionsRegistry() {
        return this.functionsRegistry;
    }

    @NotNull
    @Override
    public CalculatorMathRegistry<Operator> getOperatorsRegistry() {
        return this.operatorsRegistry;
    }

    @NotNull
    @Override
    public CalculatorMathRegistry<Operator> getPostfixFunctionsRegistry() {
        return this.postfixFunctionsRegistry;
    }

    @NotNull
    @Override
    public CalculatorMathEngine getMathEngine() {
        return this.mathEngine;
    }

    @NotNull
    @Override
    public MathEngine getMathEngine0() {
        return this.engine;
    }

    /*
    **********************************************************************
    *
    *                           INIT
    *
    **********************************************************************
    */

    @Override
    public void init() {
        synchronized (lock) {
            reset();
        }
    }

    @Override
    public void reset() {
        synchronized (lock) {
            varsRegistry.load();
            functionsRegistry.load();
            operatorsRegistry.load();
            postfixFunctionsRegistry.load();
        }
    }

    @Override
    public void softReset() {
        CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.engine_preferences_changed, null);
    }

    /*
    **********************************************************************
    *
    *                           PREFERENCES
    *
    **********************************************************************
    */

    @NotNull
    @Override
    public String getMultiplicationSign() {
        return this.multiplicationSign;
    }

    @Override
    public void setUseGroupingSeparator(boolean useGroupingSeparator) {
        synchronized (lock) {
            this.engine.setUseGroupingSeparator(true);
        }
    }

    @Override
    public void setGroupingSeparator(char groupingSeparator) {
        synchronized (lock) {
            this.engine.setGroupingSeparator(groupingSeparator);
        }
    }

    @Override
    public void setPrecision(@NotNull Integer precision) {
        synchronized (lock) {
            this.engine.setPrecision(precision);
        }
    }

    @Override
    public void setRoundResult(@NotNull Boolean round) {
        synchronized (lock) {
            this.engine.setRoundResult(round);
        }
    }

    @NotNull
    @Override
    public AngleUnit getAngleUnits() {
        synchronized (lock) {
            return this.engine.getAngleUnits();
        }
    }

    @Override
    public void setAngleUnits(@NotNull AngleUnit angleUnits) {
        synchronized (lock) {
            this.engine.setAngleUnits(angleUnits);
        }
    }

    @NotNull
    @Override
    public NumeralBase getNumeralBase() {
        synchronized (lock) {
            return this.engine.getNumeralBase();
        }
    }

    @Override
    public void setNumeralBase(@NotNull NumeralBase numeralBase) {
        synchronized (lock) {
            this.engine.setNumeralBase(numeralBase);
        }
    }

    @Override
    public void setMultiplicationSign(@NotNull String multiplicationSign) {
        this.multiplicationSign = multiplicationSign;
    }

    @Override
    public void setScienceNotation(@NotNull Boolean scienceNotation) {
        synchronized (lock) {
            this.engine.setScienceNotation(scienceNotation);
        }
    }

    @Override
    public void setTimeout(@NotNull Integer timeout) {
        this.timeout = timeout;
    }

    @Override
    public void setDecimalGroupSymbols(@NotNull DecimalFormatSymbols decimalGroupSymbols) {
        synchronized (lock) {
            this.engine.setDecimalGroupSymbols(decimalGroupSymbols);
        }
    }

    /*
    **********************************************************************
    *
    *                           STATIC CLASSES
    *
    **********************************************************************
    */

    private static final class JsclCalculatorMathEngine implements CalculatorMathEngine  {

        @NotNull
        private final MathEngine mathEngine;

        private JsclCalculatorMathEngine(@NotNull MathEngine mathEngine) {
            this.mathEngine = mathEngine;
        }

        @NotNull
        @Override
        public String evaluate(@NotNull String expression) throws ParseException {
            return this.mathEngine.evaluate(expression);
        }

        @NotNull
        @Override
        public String simplify(@NotNull String expression) throws ParseException {
            return this.mathEngine.simplify(expression);
        }

        @NotNull
        @Override
        public String elementary(@NotNull String expression) throws ParseException {
            return this.mathEngine.elementary(expression);
        }

        @NotNull
        @Override
        public Generic evaluateGeneric(@NotNull String expression) throws ParseException {
            return this.mathEngine.evaluateGeneric(expression);
        }

        @NotNull
        @Override
        public Generic simplifyGeneric(@NotNull String expression) throws ParseException {
            return this.mathEngine.simplifyGeneric(expression);
        }

        @NotNull
        @Override
        public Generic elementaryGeneric(@NotNull String expression) throws ParseException {
            return this.mathEngine.elementaryGeneric(expression);
        }
    }
}
