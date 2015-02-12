package org.solovyev.android.calculator.ga;

import android.text.TextUtils;

import org.solovyev.android.calculator.CalculatorButton;
import org.solovyev.android.calculator.CalculatorEngine;
import org.solovyev.android.calculator.Locator;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;

public final class GaButtonFilter {

	@Nonnull
	private final Set<String> operations = new HashSet<>();

	@Nonnull
	private final Set<String> functions = new HashSet<>();

	@Nonnull
	private final Set<String> variables = new HashSet<>();

	@Nullable
	String filter(@Nonnull String text) {
		String label = toOperation(text);
		if (TextUtils.isEmpty(label)) {
			label = toFunction(text);
		}
		if (TextUtils.isEmpty(label)) {
			label = toVariable(text);
		}
		return label;
	}

	@Nullable
	private String toVariable(@Nonnull String text) {
		if (variables.isEmpty()) {
			final CalculatorEngine engine = Locator.getInstance().getEngine();
			for (IConstant constant : engine.getVarsRegistry().getEntities()) {
				if (constant.isSystem()) {
					variables.add(constant.getName());
				}
			}
		}
		return variables.contains(text) ? text : null;
	}

	@Nullable
	private String toOperation(@Nonnull String text) {
		if (operations.isEmpty()) {
			for (CalculatorButton button : CalculatorButton.values()) {
				operations.add(button.getOnClickText());
				final String onLongClickText = button.getOnLongClickText();
				if (!TextUtils.isEmpty(onLongClickText)) {
					operations.add(onLongClickText);
				}
			}
		}
		return operations.contains(text) ? text : null;
	}

	@Nullable
	private String toFunction(@Nonnull String text) {
		if (functions.isEmpty()) {
			final CalculatorEngine engine = Locator.getInstance().getEngine();
			for (Function function : engine.getFunctionsRegistry().getEntities()) {
				if (function.isSystem()) {
					functions.add(function.getName());
				}
			}

			for (Operator postfixFunction : engine.getPostfixFunctionsRegistry().getEntities()) {
				if (postfixFunction.isSystem()) {
					functions.add(postfixFunction.getName());
				}
			}

			for (Operator operator : engine.getOperatorsRegistry().getEntities()) {
				if (operator.isSystem()) {
					functions.add(operator.getName());
				}
			}
		}
		if (text.endsWith("()")) {
			text = text.substring(0, text.length() - 2);
		}
		return functions.contains(text) ? text : null;
	}
}
