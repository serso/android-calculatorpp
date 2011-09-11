package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.solovyev.util.math.MathEntityType;

public class Preprocessor {

	@NotNull
	public static String process(@NotNull String s) {
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);

			checkMultiplicationSignBeforeFunction(sb, s, i);

			if (ch == '[' || ch == '{') {
				sb.append('(');
			} else if (ch == ']' || ch == '}') {
				sb.append(')');
			} else if (ch == 'π') {
				sb.append("pi");
			} else if (ch == '×' || ch == '∙') {
				sb.append("*");
			} else if (s.startsWith("ln", i)) {
				sb.append("log");
				i += 1;
			} else if (s.startsWith("tg", i)) {
				sb.append("tan");
				i += 1;
			} else if (s.startsWith("atg", i)) {
				sb.append("atan");
				i += 2;
			} else if (s.startsWith("e(", i)) {
				sb.append("exp(");
				i += 1;
			} else if (ch == 'e') {
				sb.append("exp(1)");
			} else if (ch == '√') {
				sb.append("sqrt");
			} else {
				sb.append(ch);
			}
		}

		return sb.toString();
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
						!MathEntityType.openGroupSymbols.contains(chBefore)) {

				if (mathType == MathEntityType.constant) {
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
