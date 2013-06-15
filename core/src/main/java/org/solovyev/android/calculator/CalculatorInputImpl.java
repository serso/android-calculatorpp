package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 7:26 PM
 */
public class CalculatorInputImpl implements CalculatorInput {

	@Nonnull
	private String expression;

	@Nonnull
	private JsclOperation operation;

	public CalculatorInputImpl(@Nonnull String expression, @Nonnull JsclOperation operation) {
		this.expression = expression;
		this.operation = operation;
	}

	@Override
	@Nonnull
	public String getExpression() {
		return expression;
	}

	@Override
	@Nonnull
	public JsclOperation getOperation() {
		return operation;
	}
}
