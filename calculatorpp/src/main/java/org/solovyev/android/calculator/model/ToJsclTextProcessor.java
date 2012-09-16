/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import jscl.math.function.IConstant;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.StartsWithFinder;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.collections.CollectionsUtils;

import java.util.ArrayList;
import java.util.List;

public class ToJsclTextProcessor implements TextProcessor<PreparedExpression, String> {

	@NotNull
	private static final Integer MAX_DEPTH = 20;

    @NotNull
    private static final TextProcessor<PreparedExpression, String> instance = new ToJsclTextProcessor();

    private ToJsclTextProcessor() {
    }


    @NotNull
    public static TextProcessor<PreparedExpression, String> getInstance() {
        return instance;
    }

    @Override
	@NotNull
	public PreparedExpression process(@NotNull String s) throws CalculatorParseException {
		return processWithDepth(s, 0, new ArrayList<IConstant>());
	}

	private static PreparedExpression processWithDepth(@NotNull String s, int depth, @NotNull List<IConstant> undefinedVars) throws CalculatorParseException {
		return replaceVariables(processExpression(s).toString(), depth, undefinedVars);
	}

	@NotNull
	private static StringBuilder processExpression(@NotNull String s) throws CalculatorParseException {
		final StartsWithFinder startsWithFinder = new StartsWithFinder(s, 0);
		final StringBuilder result = new StringBuilder();

		MathType.Result mathTypeResult = null;
		MathType.Result mathTypeBefore;

		final LiteNumberBuilder nb = new LiteNumberBuilder(CalculatorEngine.instance.getEngine());
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ' ') continue;
			startsWithFinder.setI(i);

			mathTypeBefore = mathTypeResult == null ? null : mathTypeResult;

			mathTypeResult = MathType.getType(s, i, nb.isHexMode());

			nb.process(mathTypeResult);

			if (mathTypeBefore != null) {

				final MathType current = mathTypeResult.getMathType();

				if (current.isNeedMultiplicationSignBefore(mathTypeBefore.getMathType())) {
					result.append("*");
				}
			}

			if (mathTypeBefore != null &&
					(mathTypeBefore.getMathType() == MathType.function || mathTypeBefore.getMathType() == MathType.operator) &&
						CollectionsUtils.find(MathType.openGroupSymbols, startsWithFinder) != null) {
				throw new CalculatorParseException(Messages.msg_5, i, s, mathTypeBefore.getMatch());
			}

			i = mathTypeResult.processToJscl(result, i);
		}
		return result;
	}

	@NotNull
	private static PreparedExpression replaceVariables(@NotNull final String s, int depth, @NotNull List<IConstant> undefinedVars) throws CalculatorParseException {
		if (depth >= MAX_DEPTH) {
			throw new CalculatorParseException(Messages.msg_6, s);
		} else {
			depth++;
		}

		final StartsWithFinder startsWithFinder = new StartsWithFinder(s, 0);

		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			startsWithFinder.setI(i);

			int offset = 0;
			String functionName = CollectionsUtils.find(MathType.function.getTokens(), startsWithFinder);
			if (functionName == null) {
				String operatorName = CollectionsUtils.find(MathType.operator.getTokens(), startsWithFinder);
				if (operatorName == null) {
					String varName = CollectionsUtils.find(CalculatorEngine.instance.getVarsRegistry().getNames(), startsWithFinder);
					if (varName != null) {
						final IConstant var = CalculatorEngine.instance.getVarsRegistry().get(varName);
						if (var != null) {
							if (!var.isDefined()) {
								undefinedVars.add(var);
								result.append(varName);
								offset = varName.length();
							} else {
								final String value = var.getValue();
								assert value != null;

								if ( var.getDoubleValue() != null ) {
									//result.append(value);
									// NOTE: append varName as JSCL engine will convert it to double if needed
									result.append(varName);
								} else {
									result.append("(").append(processWithDepth(value, depth, undefinedVars)).append(")");
								}
								offset = varName.length();
							}
						}
					}
				} else {
					result.append(operatorName);
					offset = operatorName.length();
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
}
