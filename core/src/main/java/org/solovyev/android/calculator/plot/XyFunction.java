package org.solovyev.android.calculator.plot;

import jscl.math.Generic;
import jscl.math.function.Constant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.text.Strings;

public class XyFunction implements FunctionEvaluator {

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	@NotNull
	private final String id;

    @NotNull
    private Generic expression;

    @NotNull
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

    @NotNull
    private final FunctionEvaluator evaluator;

	public XyFunction(@NotNull Generic expression,
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
        if ( this.yVariableName == null ) {
            this.arity--;
        }
        if ( this.xVariableName == null ) {
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

    @NotNull
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

    @NotNull
	public String getExpressionString() {
		return expressionString;
	}

	@NotNull
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

		if (!id.equals(that.id)) return false;

		return true;
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

        @NotNull
        protected final XyFunction xyFunction;

        @Nullable
        private Double constant = null;

        public AbstractEvaluator(@NotNull XyFunction xyFunction) {
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

        private RealEvaluator(@NotNull XyFunction xyFunction) {
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

        private ImaginaryEvaluator(@NotNull XyFunction xyFunction) {
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
