package org.solovyev.android.calculator.plot;

import jscl.math.Generic;
import jscl.math.function.Constant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XyFunction {

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

    @NotNull
    private final Generic expression;

    @NotNull
    private String expressionString;

    @Nullable
    private final Constant xVariable;

    @Nullable
    private String xVariableName;

    @Nullable
    private final Constant yVariable;

    private final boolean imag;

    @Nullable
	private String yVariableName;

    private int arity;

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
        } else {
            this.expressionString = expression.toString();
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

    }

    public boolean isImag() {
        return imag;
    }

    public int getArity() {
        return arity;
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

		if (!expressionString.equals(that.expressionString)) return false;
		if (xVariableName != null ? !xVariableName.equals(that.xVariableName) : that.xVariableName != null)
			return false;
		if (yVariableName != null ? !yVariableName.equals(that.yVariableName) : that.yVariableName != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = expressionString.hashCode();
		result = 31 * result + (xVariableName != null ? xVariableName.hashCode() : 0);
		result = 31 * result + (yVariableName != null ? yVariableName.hashCode() : 0);
		return result;
	}
}
