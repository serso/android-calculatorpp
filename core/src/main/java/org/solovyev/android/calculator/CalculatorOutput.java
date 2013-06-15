package org.solovyev.android.calculator;

import jscl.math.Generic;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 7:29 PM
 */
public interface CalculatorOutput {

	@Nonnull
	String getStringResult();

	@Nonnull
	JsclOperation getOperation();


	// null in case of empty expression
	@Nullable
	Generic getResult();
}
