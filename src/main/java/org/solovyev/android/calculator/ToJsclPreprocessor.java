/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
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
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if ( MathEntityType.getType(ch) == MathEntityType.postfix_function ) {
		   		int start = getPostfixFunctionStart(s, i - 1);
			}
		}

		final StartsWithFinder startsWithFinder = new StartsWithFinder(s);
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);

			checkMultiplicationSignBeforeFunction(sb, s, i);

			if (MathEntityType.openGroupSymbols.contains(ch)) {
				sb.append('(');
			} else if (MathEntityType.closeGroupSymbols.contains(ch)) {
				sb.append(')');
			} else if (ch == 'π') {
				sb.append("pi");
			} else if (ch == '×' || ch == '∙') {
				sb.append("*");
			} else {
				startsWithFinder.setI(i);
				final String function = CollectionsUtils.get(MathEntityType.prefixFunctions, startsWithFinder);
				if (function != null) {
					sb.append(toJsclFunction(function));
					i += function.length() - 1;
				} else if (ch == 'e') {
					sb.append("exp(1)");
				} else if (ch == 'i') {
					sb.append("sqrt(-1)");
				} else {
					sb.append(ch);
				}
			}
		}

		return sb.toString();
	}

	public int getPostfixFunctionStart(@NotNull String s, int position) {
		assert s.length() > position;

		int numberOfOpenGroups = 0;
		int result = position;
		for ( ; result >= 0; result-- ) {
			char ch = s.charAt(result);

			final MathEntityType mathEntityType = MathEntityType.getType(ch);

			if ( mathEntityType != null  ) {
				if ( CollectionsUtils.contains(mathEntityType, MathEntityType.digit, MathEntityType.dot) ) {
					// continue
				} else if (MathEntityType.closeGroupSymbols.contains(ch)) {
					numberOfOpenGroups++;
				} else if (MathEntityType.openGroupSymbols.contains(ch)) {
					numberOfOpenGroups--;
				} else {
					if (stop(s, numberOfOpenGroups, result)) break;
				}
			} else {
				if (stop(s, numberOfOpenGroups, result)) break;
			}
		}

		return result;
	}

	private boolean stop(String s, int numberOfOpenGroups, int i) {
		if ( numberOfOpenGroups == 0 ) {
			if (i > 0) {
				final EndsWithFinder endsWithFinder = new EndsWithFinder(s);
				endsWithFinder.setI(i+1);
				if ( !CollectionsUtils.contains(MathEntityType.prefixFunctions, FilterType.included, endsWithFinder) ) {
					MathEntityType type = MathEntityType.getType(s.charAt(i));
					if (type != null && type != MathEntityType.constant) {
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

	private static class StartsWithFinder implements Finder<String> {

		private int i;

		@NotNull
		private final String targetString;

		private StartsWithFinder(@NotNull String targetString) {
			this.targetString = targetString;
		}

		@Override
		public boolean isFound(@Nullable String s) {
			return targetString.startsWith(s, i);
		}

		public void setI(int i) {
			this.i = i;
		}
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

	private static void checkMultiplicationSignBeforeFunction(@NotNull StringBuilder sb, @NotNull String s, int i) {
		if (i > 0) {
			// get character before function
			char chBefore = s.charAt(i - 1);
			char ch = s.charAt(i);

			final MathEntityType mathTypeBefore = MathEntityType.getType(String.valueOf(chBefore));
			final MathEntityType mathType = MathEntityType.getType(String.valueOf(ch));

			if (mathTypeBefore != MathEntityType.binary_operation &&
					mathTypeBefore != MathEntityType.unary_operation &&
						mathTypeBefore != MathEntityType.function &&
							!MathEntityType.openGroupSymbols.contains(chBefore)) {

				if (mathType == MathEntityType.constant) {
					sb.append("*");
				} else if (MathEntityType.openGroupSymbols.contains(ch) && mathTypeBefore != null) {
					sb.append("*");
				} else if (mathType == MathEntityType.digit && mathTypeBefore != MathEntityType.digit && mathTypeBefore != MathEntityType.dot) {
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
	}

	public static String wrap(@NotNull JsclOperation operation, @NotNull String s) {
		return operation.name() + "(\"" + s + "\");";
	}
}
