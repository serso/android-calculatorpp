package org.solovyev.android.calculator;

import jscl.math.Generic;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;

import java.io.Serializable;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 9:50 PM
 */
public interface CalculatorDisplayViewState extends Serializable {

	@Nonnull
	String getText();

	int getSelection();

	@Nullable
	Generic getResult();

	boolean isValid();

	@Nullable
	String getErrorMessage();

	@Nonnull
	JsclOperation getOperation();

	@Nullable
	String getStringResult();
}
