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

package org.solovyev.android.calculator.plot;

import jscl.math.Generic;
import jscl.math.function.Constant;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class XyFunction implements FunctionEvaluator {

	/*
    **********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

    @Nonnull
    private final String id;
    @Nonnull
    private final FunctionEvaluator evaluator;
    @Nonnull
    private Generic expression;
    @Nonnull
    private String expressionString;
    @Nullable
    private Constant xVariable;
    @Nullable
    private String xVariableName;
    @Nullable
    private Constant yVariable;
    private boolean imag;
    @Nullable
    private String yVariableName;
    private int arity;

    public XyFunction(@Nonnull Generic expression,
                      @Nullable Constant xVariable,
                      @Nullable Constant yVariable,
                      boolean imag) {
        this.expression = expression;
        this.xVariable = xVariable;
        this.yVariable = yVariable;
        this.imag = imag;

        if (imag) {
            this.expressionString = "Im(" + expression.toString() + ")";
            this.evaluator = new ImaginaryEvaluator(this);
        } else {
            this.expressionString = expression.toString();
            this.evaluator = new RealEvaluator(this);
        }
        this.xVariableName = xVariable == null ? null : xVariable.getName();
        this.yVariableName = yVariable == null ? null : yVariable.getName();

        this.arity = 2;
        if (this.yVariableName == null) {
            this.arity--;
        }
        if (this.xVariableName == null) {
            this.arity--;
        }

        this.id = this.expressionString + "_" + Strings.getNotEmpty(this.xVariableName, "") + "_" + Strings.getNotEmpty(this.yVariableName, "");

    }

    public boolean isImag() {
        return imag;
    }

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    public double eval() {
        return evaluator.eval();
    }

    @Override
    public double eval(double x) {
        return evaluator.eval(x);
    }

    @Override
    public double eval(double x, double y) {
        return evaluator.eval(x, y);
    }

    @Nonnull
    public Generic getExpression() {
        return expression;
    }

    @Nullable
    public Constant getXVariable() {
        return xVariable;
    }

    @Nullable
    public Constant getYVariable() {
        return yVariable;
    }

    @Nonnull
    public String getExpressionString() {
        return expressionString;
    }

    @Nonnull
    public String getId() {
        return id;
    }

    @Nullable
    public String getXVariableName() {
        return xVariableName;
    }

    @Nullable
    public String getYVariableName() {
        return yVariableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XyFunction)) return false;

        final XyFunction that = (XyFunction) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

	/*
	**********************************************************************
	*
	*                           STATIC
	*
	**********************************************************************
	*/

    private static abstract class AbstractEvaluator implements FunctionEvaluator {

        @Nonnull
        protected final XyFunction xyFunction;

        @Nullable
        private Double constant = null;

        public AbstractEvaluator(@Nonnull XyFunction xyFunction) {
            this.xyFunction = xyFunction;
        }

        @Override
        public final double eval() {
            if (constant == null) {
                constant = eval0();
            }
            return constant;
        }

        protected abstract double eval0();

        @Override
        public final int getArity() {
            return xyFunction.getArity();
        }

    }

    private static class RealEvaluator extends AbstractEvaluator {

        private RealEvaluator(@Nonnull XyFunction xyFunction) {
            super(xyFunction);
        }

        @Override
        public double eval0() {
            return PlotUtils.calculatorExpression(xyFunction.expression).realPart();
        }

        @Override
        public double eval(double x) {
            return PlotUtils.calculatorExpression(xyFunction.expression, xyFunction.xVariable, x).realPart();
        }

        @Override
        public double eval(double x, double y) {
            return PlotUtils.calculatorExpression(xyFunction.expression, xyFunction.xVariable, x, xyFunction.yVariable, y).realPart();
        }
    }

    private static class ImaginaryEvaluator extends AbstractEvaluator {

        private ImaginaryEvaluator(@Nonnull XyFunction xyFunction) {
            super(xyFunction);
        }

        @Override
        public double eval0() {
            return PlotUtils.calculatorExpression(xyFunction.expression).imaginaryPart();
        }

        @Override
        public double eval(double x) {
            return PlotUtils.calculatorExpression(xyFunction.expression, xyFunction.xVariable, x).imaginaryPart();
        }

        @Override
        public double eval(double x, double y) {
            return PlotUtils.calculatorExpression(xyFunction.expression, xyFunction.xVariable, x, xyFunction.yVariable, y).imaginaryPart();
        }
    }
}
