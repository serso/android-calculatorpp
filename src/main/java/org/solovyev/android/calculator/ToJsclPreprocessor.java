/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.math.Functions;
import org.solovyev.android.calculator.math.MathEntityType;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.FilterType;
import org.solovyev.common.utils.Finder;

public class ToJsclPreprocessor implements Preprocessor {

	@Override
	@NotNull
	public String process(@NotNull String s) {

		final StartsWithFinder startsWithFinder = new StartsWithFinder(s, 0);
		final StringBuilder sb = new StringBuilder();

		MathEntityType.Result mathTypeResult = null;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			startsWithFinder.setI(i);

			mathTypeResult = checkMultiplicationSignBeforeFunction(sb, s, i, mathTypeResult);

			final MathEntityType mathType = mathTypeResult.getMathEntityType();
			if (mathType == MathEntityType.open_group_symbol) {
				sb.append('(');
			} else if (mathType == MathEntityType.close_group_symbol) {
				sb.append(')');
			} else if (ch == '×' || ch == '∙') {
				sb.append("*");
			} else if ( mathType == MathEntityType.function  ){
				sb.append(toJsclFunction(mathTypeResult.getS()));
				i += mathTypeResult.getS().length() - 1;
			} else if ( mathType == MathEntityType.constant ) {
				sb.append(mathTypeResult.getS());
				i += mathTypeResult.getS().length() - 1;
			} else {
				sb.append(ch);
			}
		}

		return replaceVariables(sb.toString());
	}

	private String replaceVariables(@NotNull final String s) {
		final StartsWithFinder startsWithFinder = new StartsWithFinder(s, 0);

		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			startsWithFinder.setI(i);

			int offset = 0;
			String functionName = CollectionsUtils.get(MathEntityType.prefixFunctions, startsWithFinder);
			if (functionName == null) {
				String varName = CollectionsUtils.get(CalculatorModel.getInstance().getVarsRegister().getVarNames(), startsWithFinder);
				if (varName != null) {
					final Var var = CalculatorModel.getInstance().getVarsRegister().getVar(varName);
					if (var != null) {
						result.append(var.getValue());
						offset = varName.length();
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

		return result.toString();
	}

	private void replaceVariables(StringBuilder sb, String s, int i, @NotNull StartsWithFinder startsWithFinder) {
		for (Var var : CalculatorModel.getInstance().getVarsRegister().getVars()) {
			if (!var.isSystem()) {
				if (s.startsWith(var.getName(), i)) {
					if (CollectionsUtils.get(MathEntityType.prefixFunctions, startsWithFinder) == null) {
					}
				}
			}
		}
	}

	public int getPostfixFunctionStart(@NotNull String s, int position) {
		assert s.length() > position;

		int numberOfOpenGroups = 0;
		int result = position;
		for (; result >= 0; result--) {

			final MathEntityType mathEntityType = MathEntityType.getMathEntityType(s, result);

			if (CollectionsUtils.contains(mathEntityType, MathEntityType.digit, MathEntityType.dot)) {
				// continue
			} else if (mathEntityType == MathEntityType.close_group_symbol) {
				numberOfOpenGroups++;
			} else if (mathEntityType == MathEntityType.open_group_symbol) {
				numberOfOpenGroups--;
			} else {
				if (stop(s, numberOfOpenGroups, result)) break;
			}
		}

		return result;
	}

	private boolean stop(String s, int numberOfOpenGroups, int i) {
		if (numberOfOpenGroups == 0) {
			if (i > 0) {
				final EndsWithFinder endsWithFinder = new EndsWithFinder(s);
				endsWithFinder.setI(i + 1);
				if (!CollectionsUtils.contains(MathEntityType.prefixFunctions, FilterType.included, endsWithFinder)) {
					MathEntityType type = MathEntityType.getMathEntityType(s, i);
					if (type != MathEntityType.constant) {
						return true;
					}
				}
			} else {
				return true;
			}
		}

		return false;
	}

	@NotNull
	private static String toJsclFunction(@NotNull String function) {
		final String result;

		if (function.equals(Functions.LN)) {
			result = Functions.LOG;
		} else if (function.equals(Functions.SQRT_SIGN)) {
			result = Functions.SQRT;
		} else {
			result = function;
		}

		return result;
	}

	private static class EndsWithFinder implements Finder<String> {

		private int i;

		@NotNull
		private final String targetString;

		private EndsWithFinder(@NotNull String targetString) {
			this.targetString = targetString;
		}

		@Override
		public boolean isFound(@Nullable String s) {
			return targetString.substring(0, i).endsWith(s);
		}

		public void setI(int i) {
			this.i = i;
		}
	}

	@NotNull
	private static MathEntityType.Result checkMultiplicationSignBeforeFunction(@NotNull StringBuilder sb,
																		@NotNull String s,
																		int i,
																		@Nullable MathEntityType.Result mathTypeBeforeResult) {
		MathEntityType.Result result = MathEntityType.getType(s, i);

		if (i > 0) {

			final MathEntityType mathType = result.getMathEntityType();
			assert mathTypeBeforeResult != null;
			final MathEntityType mathTypeBefore = mathTypeBeforeResult.getMathEntityType();

			if (mathTypeBefore == MathEntityType.constant || (mathTypeBefore != MathEntityType.binary_operation &&
					mathTypeBefore != MathEntityType.unary_operation &&
					mathTypeBefore != MathEntityType.function &&
					mathTypeBefore != MathEntityType.open_group_symbol)) {

				if (mathType == MathEntityType.constant) {
					sb.append("*");
				} else if (mathType == MathEntityType.open_group_symbol && mathTypeBefore != null) {
					sb.append("*");
				} else if (mathType == MathEntityType.digit && ((mathTypeBefore != MathEntityType.digit && mathTypeBefore != MathEntityType.dot) || mathTypeBefore == MathEntityType.constant)) {
					sb.append("*");
				} else {
					for (String function : MathEntityType.prefixFunctions) {
						if (s.startsWith(function, i)) {
							sb.append("*");
							break;
						}
					}
				}
			}
		}

		return result;
	}

	public static String wrap(@NotNull JsclOperation operation, @NotNull String s) {
		return operation.name() + "(\"" + s + "\");";
	}
}
