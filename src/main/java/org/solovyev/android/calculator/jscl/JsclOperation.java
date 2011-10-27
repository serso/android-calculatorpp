/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.jscl;


import jscl.math.Expression;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.DummyTextProcessor;
import org.solovyev.android.calculator.model.FromJsclSimplifyTextProcessor;
import org.solovyev.android.calculator.model.TextProcessor;

public enum JsclOperation {

	simplify(new FromJsclSimplifyTextProcessor()) {
		@NotNull
		@Override
		public String evaluate(@NotNull Expression expression) {
			return expression.simplify().toString();
		}
	},

	elementary(DummyTextProcessor.instance) {
		@NotNull
		@Override
		public String evaluate(@NotNull Expression expression) {
			return expression.elementary().toString();

		}
	},

	numeric(new FromJsclNumericTextProcessor()) {
		@NotNull
		@Override
		public String evaluate(@NotNull Expression expression) {
			return expression.numeric().toString();
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
	public abstract String evaluate(@NotNull Expression expression);
}
