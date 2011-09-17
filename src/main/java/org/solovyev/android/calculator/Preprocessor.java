/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.EqualsFinder;
import org.solovyev.common.utils.Finder;
import org.solovyev.util.math.Functions;
import org.solovyev.util.math.MathEntityType;

public class Preprocessor {

	@NotNull
	public static String process(@NotNull String s) {
		final StringBuilder sb = new StringBuilder();

		final StartWithFinder startsWithFinder = new StartWithFinder(s);
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
				final String function = CollectionsUtils.get(MathEntityType.functions, startsWithFinder);
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

	private static class StartWithFinder implements Finder<String> {

		private int i;

		@NotNull
		private final String targetString;

		private StartWithFinder(@NotNull String targetString) {
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
					for (String function : MathEntityType.functions) {
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
