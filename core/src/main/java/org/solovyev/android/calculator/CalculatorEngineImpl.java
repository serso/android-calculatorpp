/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import java.text.DecimalFormatSymbols;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.Generic;
import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;
import jscl.text.ParseException;

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
    @Nonnull
    private final MathEngine engine;

    @Nonnull
    private final CalculatorMathEngine mathEngine;

    @Nonnull
    private final CalculatorMathRegistry<IConstant> varsRegistry;

    @Nonnull
    private final CalculatorMathRegistry<Function> functionsRegistry;

    @Nonnull
    private final CalculatorMathRegistry<Operator> operatorsRegistry;

    @Nonnull
    private final CalculatorMathRegistry<Operator> postfixFunctionsRegistry;

    @Nonnull
    private final Object lock;

	/*
	**********************************************************************
	*
	*                           PREFERENCES
	*
	**********************************************************************
	*/


    private int timeout = Integer.valueOf(MAX_CALCULATION_TIME_DEFAULT);

    @Nonnull
    private String multiplicationSign = MULTIPLICATION_SIGN_DEFAULT;

    public CalculatorEngineImpl(@Nonnull JsclMathEngine engine,
                                @Nonnull CalculatorMathRegistry<IConstant> varsRegistry,
                                @Nonnull CalculatorMathRegistry<Function> functionsRegistry,
                                @Nonnull CalculatorMathRegistry<Operator> operatorsRegistry,
                                @Nonnull CalculatorMathRegistry<Operator> postfixFunctionsRegistry,
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
    @Nonnull
    @Override
    public CalculatorMathRegistry<IConstant> getVarsRegistry() {
        return this.varsRegistry;
    }

    @Nonnull
    @Override
    public CalculatorMathRegistry<Function> getFunctionsRegistry() {
        return this.functionsRegistry;
    }

    @Nonnull
    @Override
    public CalculatorMathRegistry<Operator> getOperatorsRegistry() {
        return this.operatorsRegistry;
    }

    @Nonnull
    @Override
    public CalculatorMathRegistry<Operator> getPostfixFunctionsRegistry() {
        return this.postfixFunctionsRegistry;
    }

    @Nonnull
    @Override
    public CalculatorMathEngine getMathEngine() {
        return this.mathEngine;
    }

    @Nonnull
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
            safeLoadRegistry(varsRegistry);
            safeLoadRegistry(functionsRegistry);
            safeLoadRegistry(operatorsRegistry);
            safeLoadRegistry(postfixFunctionsRegistry);
        }
    }

    private void safeLoadRegistry(@Nonnull CalculatorMathRegistry<?> registry) {
        try {
            registry.load();
        } catch (Exception e) {
            logException(e);
        }
    }

    private void logException(@Nonnull Exception e) {
        final CalculatorLogger logger = Locator.getInstance().getLogger();
        logger.error("Engine", e.getMessage(), e);
    }

    @Override
    public void softReset() {
        Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.engine_preferences_changed, null);
    }

	/*
	**********************************************************************
	*
	*                           PREFERENCES
	*
	**********************************************************************
	*/

    @Nonnull
    @Override
    public String getMultiplicationSign() {
        return this.multiplicationSign;
    }

    @Override
    public void setMultiplicationSign(@Nonnull String multiplicationSign) {
        this.multiplicationSign = multiplicationSign;
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
    public void setPrecision(@Nonnull Integer precision) {
        synchronized (lock) {
            this.engine.setPrecision(precision);
        }
    }

    @Override
    public void setRoundResult(@Nonnull Boolean round) {
        synchronized (lock) {
            this.engine.setRoundResult(round);
        }
    }

    @Nonnull
    @Override
    public AngleUnit getAngleUnits() {
        synchronized (lock) {
            return this.engine.getAngleUnits();
        }
    }

    @Override
    public void setAngleUnits(@Nonnull AngleUnit angleUnits) {
        synchronized (lock) {
            this.engine.setAngleUnits(angleUnits);
        }
    }

    @Nonnull
    @Override
    public NumeralBase getNumeralBase() {
        synchronized (lock) {
            return this.engine.getNumeralBase();
        }
    }

    @Override
    public void setNumeralBase(@Nonnull NumeralBase numeralBase) {
        synchronized (lock) {
            this.engine.setNumeralBase(numeralBase);
        }
    }

    @Override
    public void setScienceNotation(@Nonnull Boolean scienceNotation) {
        synchronized (lock) {
            this.engine.setScienceNotation(scienceNotation);
        }
    }

    @Override
    public void setTimeout(@Nonnull Integer timeout) {
        this.timeout = timeout;
    }

    @Override
    public void setDecimalGroupSymbols(@Nonnull DecimalFormatSymbols decimalGroupSymbols) {
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

    private static final class JsclCalculatorMathEngine implements CalculatorMathEngine {

        @Nonnull
        private final MathEngine mathEngine;

        private JsclCalculatorMathEngine(@Nonnull MathEngine mathEngine) {
            this.mathEngine = mathEngine;
        }

        @Nonnull
        @Override
        public String evaluate(@Nonnull String expression) throws ParseException {
            return this.mathEngine.evaluate(expression);
        }

        @Nonnull
        @Override
        public String simplify(@Nonnull String expression) throws ParseException {
            return this.mathEngine.simplify(expression);
        }

        @Nonnull
        @Override
        public String elementary(@Nonnull String expression) throws ParseException {
            return this.mathEngine.elementary(expression);
        }

        @Nonnull
        @Override
        public Generic evaluateGeneric(@Nonnull String expression) throws ParseException {
            return this.mathEngine.evaluateGeneric(expression);
        }

        @Nonnull
        @Override
        public Generic simplifyGeneric(@Nonnull String expression) throws ParseException {
            return this.mathEngine.simplifyGeneric(expression);
        }

        @Nonnull
        @Override
        public Generic elementaryGeneric(@Nonnull String expression) throws ParseException {
            return this.mathEngine.elementaryGeneric(expression);
        }
    }
}
