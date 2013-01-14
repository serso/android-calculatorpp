package org.solovyev.android.calculator.plot;

import jscl.math.Generic;
import jscl.math.function.Constant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.text.StringUtils;

public class XyFunction {

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

		this.id = this.expressionString + "_" + StringUtils.getNotEmpty(this.xVariableName, "") + "_" + StringUtils.getNotEmpty(this.yVariableName, "");

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
}
