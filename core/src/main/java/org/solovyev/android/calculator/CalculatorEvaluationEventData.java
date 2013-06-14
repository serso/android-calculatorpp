package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 10:00 PM
 */
public interface CalculatorEvaluationEventData extends CalculatorEventData {

	@NotNull
	JsclOperation getOperation();

	@NotNull
	String getExpression();
}
