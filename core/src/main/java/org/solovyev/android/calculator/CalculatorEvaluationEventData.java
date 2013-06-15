package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 10:00 PM
 */
public interface CalculatorEvaluationEventData extends CalculatorEventData {

	@Nonnull
	JsclOperation getOperation();

	@Nonnull
	String getExpression();
}
