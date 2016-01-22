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

import jscl.NumeralBase;
import jscl.math.Generic;
import org.solovyev.android.calculator.jscl.JsclOperation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:38
 */
public interface Calculator extends CalculatorEventContainer {

    void init(@Nonnull Executor initThread);

	/*
    **********************************************************************
	*
	*                           CALCULATIONS
	*
	**********************************************************************
	*/

    void evaluate();

    void evaluate(@Nonnull Long sequenceId);

    void simplify();

    @Nonnull
    CalculatorEventData evaluate(@Nonnull JsclOperation operation,
                                 @Nonnull String expression);

    @Nonnull
    CalculatorEventData evaluate(@Nonnull JsclOperation operation,
                                 @Nonnull String expression,
                                 long sequenceId);

    boolean isCalculateOnFly();

    void setCalculateOnFly(boolean calculateOnFly);

	/*
	**********************************************************************
	*
	*                           CONVERSION
	*
	**********************************************************************
	*/

    boolean isConversionPossible(@Nonnull Generic generic, @Nonnull NumeralBase from, @Nonnull NumeralBase to);

    @Nonnull
    CalculatorEventData convert(@Nonnull Generic generic, @Nonnull NumeralBase to);

    /*
    **********************************************************************
    *
    *                           EVENTS
    *
    **********************************************************************
    */
    @Nonnull
    CalculatorEventData fireCalculatorEvent(@Nonnull CalculatorEventType calculatorEventType, @Nullable Object data);

    @Nonnull
    CalculatorEventData fireCalculatorEvent(@Nonnull CalculatorEventType calculatorEventType, @Nullable Object data, @Nonnull Object source);

    @Nonnull
    CalculatorEventData fireCalculatorEvent(@Nonnull CalculatorEventType calculatorEventType, @Nullable Object data, @Nonnull Long sequenceId);

    @Nonnull
    PreparedExpression prepareExpression(@Nonnull String expression) throws ParseException;
}
