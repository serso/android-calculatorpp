package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 7:26 PM
 */
public class CalculatorInputImpl implements CalculatorInput {

	@NotNull
	private String expression;

	@NotNull
	private JsclOperation operation;

	public CalculatorInputImpl(@NotNull String expression, @NotNull JsclOperation operation) {
		this.expression = expression;
		this.operation = operation;
	}

	@Override
	@NotNull
	public String getExpression() {
		return expression;
	}

	@Override
	@NotNull
	public JsclOperation getOperation() {
		return operation;
	}
}
