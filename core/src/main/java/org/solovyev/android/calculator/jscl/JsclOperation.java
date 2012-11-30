/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.jscl;


import jscl.math.Generic;
import jscl.text.ParseException;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorMathEngine;
import org.solovyev.android.calculator.text.DummyTextProcessor;
import org.solovyev.android.calculator.text.FromJsclSimplifyTextProcessor;
import org.solovyev.android.calculator.text.TextProcessor;

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
	public final String evaluate(@NotNull String expression, @NotNull CalculatorMathEngine engine) throws ParseException {
		switch (this) {
			case simplify:
				return engine.simplify(expression);
			case elementary:
				return engine.elementary(expression);
			case numeric:
				return engine.evaluate(expression);
			default:
				throw new UnsupportedOperationException();
		}
	}

	@NotNull
	public final Generic evaluateGeneric(@NotNull String expression, @NotNull CalculatorMathEngine engine) throws ParseException {
		switch (this) {
			case simplify:
				return engine.simplifyGeneric(expression);
			case elementary:
				return engine.elementaryGeneric(expression);
			case numeric:
				return engine.evaluateGeneric(expression);
			default:
				throw new UnsupportedOperationException();
		}
	}


}
