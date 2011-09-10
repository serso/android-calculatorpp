package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

public class Preprocessor {

	@NotNull
	public static String process(@NotNull String s) {
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);

			if (ch == '[' || ch == '{') {
				sb.append('(');
			} else if (ch == ']' || ch == '}') {
				sb.append(')');
			} else if (ch == 'π') {
				sb.append("pi");
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

	public static String wrap(@NotNull JsclOperation operation, @NotNull String s) {
		return operation.name() + "(\"" + s + "\");";
	}
}
