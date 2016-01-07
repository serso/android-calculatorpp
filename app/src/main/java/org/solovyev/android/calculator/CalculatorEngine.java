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

import jscl.AngleUnit;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;

import javax.annotation.Nonnull;
import java.text.DecimalFormatSymbols;

public interface CalculatorEngine {

    void init();
    void reset();
    void softReset();

    @Nonnull
    CalculatorMathRegistry<IConstant> getVarsRegistry();
    @Nonnull
    CalculatorMathRegistry<Function> getFunctionsRegistry();
    @Nonnull
    CalculatorMathRegistry<Operator> getOperatorsRegistry();
    @Nonnull
    CalculatorMathRegistry<Operator> getPostfixFunctionsRegistry();
    @Nonnull
    CalculatorMathEngine getMathEngine();
    @Deprecated
    @Nonnull
    MathEngine getMathEngine0();

    @Nonnull
    String getMultiplicationSign();
    void setMultiplicationSign(@Nonnull String multiplicationSign);
    void setUseGroupingSeparator(boolean useGroupingSeparator);
    void setGroupingSeparator(char groupingSeparator);
    void setPrecision(@Nonnull Integer precision);
    void setRoundResult(@Nonnull Boolean round);
    @Nonnull
    AngleUnit getAngleUnits();
    void setAngleUnits(@Nonnull AngleUnit angleUnits);
    @Nonnull
    NumeralBase getNumeralBase();
    void setNumeralBase(@Nonnull NumeralBase numeralBase);
    void setScienceNotation(@Nonnull Boolean scienceNotation);
    void setDecimalGroupSymbols(@Nonnull DecimalFormatSymbols decimalGroupSymbols);
}
