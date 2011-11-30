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

	simplify(new FromJsclSimplifyTextProcessor()) {
		@NotNull
		@Override
		public String evaluate(@NotNull String expression) throws ParseException {
			return CalculatorEngine.instance.getEngine().simplify(expression);
		}

		@NotNull
		@Override
		public Generic evaluateGeneric(@NotNull String expression) throws ParseException {
			return CalculatorEngine.instance.getEngine().simplifyGeneric(expression);
		}
	},

	elementary(DummyTextProcessor.instance) {
		@NotNull
		@Override
		public String evaluate(@NotNull String expression) throws ParseException {
			return CalculatorEngine.instance.getEngine().elementary(expression);

		}

		@NotNull
		@Override
		public Generic evaluateGeneric(@NotNull String expression) throws ParseException {
			return CalculatorEngine.instance.getEngine().elementaryGeneric(expression);
		}
	},

	numeric(new FromJsclNumericTextProcessor()) {
		@NotNull
		@Override
		public String evaluate(@NotNull String expression) throws ParseException {
			return CalculatorEngine.instance.getEngine().evaluate(expression);
		}

		@NotNull
		@Override
		public Generic evaluateGeneric(@NotNull String expression) throws ParseException {
			return CalculatorEngine.instance.getEngine().evaluateGeneric(expression);
		}
	};

	@NotNull
	private final TextProcessor<String> fromProcessor;

	JsclOperation(@NotNull TextProcessor<String> fromProcessor) {
		this.fromProcessor = fromProcessor;
	}

	@NotNull
	public TextProcessor<String> getFromProcessor() {
		return fromProcessor;
	}

	@NotNull
	public abstract String evaluate(@NotNull String expression) throws ParseException;

	@NotNull
	public abstract Generic evaluateGeneric(@NotNull String expression) throws ParseException;


}
