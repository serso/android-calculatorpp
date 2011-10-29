/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.StartsWithFinder;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.utils.CollectionsUtils;

import java.util.ArrayList;
import java.util.List;

class ToJsclTextProcessor implements TextProcessor<PreparedExpression> {

	@Override
	@NotNull
	public PreparedExpression process(@NotNull String s) throws ParseException {

		final StartsWithFinder startsWithFinder = new StartsWithFinder(s, 0);
		final StringBuilder result = new StringBuilder();

		MathType.Result mathTypeResult = null;
		MathType mathTypeBefore;

		for (int i = 0; i < s.length(); i++) {
			startsWithFinder.setI(i);

			mathTypeBefore = mathTypeResult == null ? null : mathTypeResult.getMathType();

			mathTypeResult = MathType.getType(s, i);

			if (mathTypeBefore != null) {

				final MathType current = mathTypeResult.getMathType();

				if (current.isNeedMultiplicationSignBefore(mathTypeBefore)) {
					result.append("*");
				}
			}

			if (mathTypeBefore == MathType.function && CollectionsUtils.find(MathType.openGroupSymbols, startsWithFinder) != null) {
				throw new ParseException("Empty function: " + mathTypeResult.getMatch());
			}

			i = mathTypeResult.processToJscl(result, i);
		}

		return replaceVariables(result.toString());
	}

	@NotNull
	private PreparedExpression replaceVariables(@NotNull final String s) {
		final StartsWithFinder startsWithFinder = new StartsWithFinder(s, 0);

		final List<Var> undefinedVars = new ArrayList<Var>();

		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			startsWithFinder.setI(i);

			int offset = 0;
			String functionName = CollectionsUtils.find(MathType.function.getTokens(), startsWithFinder);
			if (functionName == null) {
				String varName = CollectionsUtils.find(CalculatorEngine.instance.getVarsRegister().getNames(), startsWithFinder);
				if (varName != null) {
					final Var var = CalculatorEngine.instance.getVarsRegister().get(varName);
					if (var != null) {
						if (var.isUndefined()) {
							undefinedVars.add(var);
							result.append(varName);
							offset = varName.length();
						} else {
							result.append(var.getValue());
							offset = varName.length();
						}
					}
				}
			} else {
				result.append(functionName);
				offset = functionName.length();
			}


			if (offset == 0) {
				result.append(s.charAt(i));
			} else {
				i += offset - 1;
			}
		}

		return new PreparedExpression(result.toString(), undefinedVars);
	}

	public static String wrap(@NotNull JsclOperation operation, @NotNull String s) {
		return operation.name() + "(\"" + s + "\");";
	}
}
