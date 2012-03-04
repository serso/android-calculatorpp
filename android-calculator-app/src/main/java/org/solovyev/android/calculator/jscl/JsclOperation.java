/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.jscl;


import jscl.math.Generic;
import jscl.text.ParseException;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.model.DummyTextProcessor;
import org.solovyev.android.calculator.model.FromJsclSimplifyTextProcessor;
import org.solovyev.android.calculator.model.TextProcessor;

public enum JsclOperation {

	simplify,
	elementary,
	numeric;

	JsclOperation() {
	}


	@NotNull
	public TextProcessor<String, Generic> getFromProcessor() {
		switch (this) {
			case simplify:
				return FromJsclSimplifyTextProcessor.instance;
			case elementary:
				return DummyTextProcessor.instance;
			case numeric:
				return FromJsclNumericTextProcessor.instance;
			default:
				throw new UnsupportedOperationException();
		}
	}

	@NotNull
	public final String evaluate(@NotNull String expression) throws ParseException {
		switch (this) {
			case simplify:
				return CalculatorEngine.instance.getEngine().simplify(expression);
			case elementary:
				return CalculatorEngine.instance.getEngine().elementary(expression);
			case numeric:
				return CalculatorEngine.instance.getEngine().evaluate(expression);
			default:
				throw new UnsupportedOperationException();
		}
	}

	@NotNull
	public final Generic evaluateGeneric(@NotNull String expression) throws ParseException {
		switch (this) {
			case simplify:
				return CalculatorEngine.instance.getEngine().simplifyGeneric(expression);
			case elementary:
				return CalculatorEngine.instance.getEngine().elementaryGeneric(expression);
			case numeric:
				return CalculatorEngine.instance.getEngine().evaluateGeneric(expression);
			default:
				throw new UnsupportedOperationException();
		}
	}


}
