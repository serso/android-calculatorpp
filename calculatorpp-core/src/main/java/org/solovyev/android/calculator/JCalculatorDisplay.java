/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.Editor;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 12/17/11
 * Time: 9:45 PM
 */
public interface JCalculatorDisplay extends Editor{

	boolean isValid();

	void setValid(boolean valid);

	@Nullable
	String getErrorMessage();

	void setErrorMessage(@Nullable String errorMessage);

	void setJsclOperation(@NotNull JsclOperation jsclOperation);

	@NotNull
	JsclOperation getJsclOperation();

	void setGenericResult(@Nullable Generic genericResult);

	@Nullable
	Generic getGenericResult();
}
