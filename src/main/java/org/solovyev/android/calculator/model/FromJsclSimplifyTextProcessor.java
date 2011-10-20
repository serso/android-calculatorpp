package org.solovyev.android.calculator.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.StartsWithFinder;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Finder;

/**
 * User: serso
 * Date: 10/20/11
 * Time: 2:59 PM
 */
public class FromJsclSimplifyTextProcessor implements TextProcessor<String> {

	@NotNull
	@Override
	public String process(@NotNull String s) throws ParseException {
		final StringBuilder sb = new StringBuilder();

		MathType.Result mathTypeResult = null;
		StringBuilder numberBuilder = null;
		String number = null;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);

			mathTypeResult = MathType.getType(s, i);

			final MathType mathType = mathTypeResult.getMathType();

			number = null;
			if (mathType == MathType.digit || mathType == MathType.dot || mathType == MathType.power_10) {
				if (numberBuilder == null) {
					numberBuilder = new StringBuilder();
				}
				numberBuilder.append(mathTypeResult.getMatch());
			} else {
				if (numberBuilder != null) {
					try {
						number = numberBuilder.toString();
						Double.valueOf(number);
					} catch (NumberFormatException e) {
						number = null;
					}

					numberBuilder = null;
				}
			}

			replaceSystemVars(sb, number);

			if (mathType == MathType.constant) {
				sb.append(mathTypeResult.getMatch());
				i += mathTypeResult.getMatch().length() - 1;
			} else if (ch == '*') {
				sb.append("×");
			} else {
				sb.append(ch);
			}
		}

		if (numberBuilder != null) {
			try {
				number = numberBuilder.toString();
				Double.valueOf(number);
			} catch (NumberFormatException e) {
				number = null;
			}

			numberBuilder = null;
		}

		replaceSystemVars(sb, number);

		return sb.toString();
	}

	private void replaceSystemVars(StringBuilder sb, String number) {
		if (number != null) {
			final String finalNumber = number;
			final Var var = CollectionsUtils.get(CalculatorEngine.instance.getVarsRegister().getSystemVars(), new Finder<Var>() {
				@Override
				public boolean isFound(@Nullable Var var) {
					return var != null && finalNumber.equals(var.getValue());
				}
			});

			if (var != null) {
				sb.delete(sb.length() - number.length(), sb.length());
				sb.append(var.getName());
			}
		}
	}

}
