package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 7:25 PM
 */
public interface CalculatorInput {

	@Nonnull
	String getExpression();

	@Nonnull
	JsclOperation getOperation();
}
