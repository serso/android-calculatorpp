/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.jscl;


import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.DummyTextProcessor;
import org.solovyev.android.calculator.model.TextProcessor;

public enum JsclOperation {

	simplify(DummyTextProcessor.instance),
	elementary(DummyTextProcessor.instance),
	importCommands(DummyTextProcessor.instance),
	numeric(new FromJsclNumericTextProcessor());

	@NotNull
	private final TextProcessor<String> fromProcessor;

	JsclOperation(@NotNull TextProcessor<String> fromProcessor) {
		this.fromProcessor = fromProcessor;
	}

	@NotNull
	public TextProcessor<String> getFromProcessor() {
		return fromProcessor;
	}
}
