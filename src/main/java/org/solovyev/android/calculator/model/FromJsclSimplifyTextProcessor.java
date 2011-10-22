package org.solovyev.android.calculator.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.math.Functions;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Finder;

import java.util.Arrays;
import java.util.List;

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
			if ( Character.isWhitespace(ch) ) {
				continue;
			}

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
				} else {
					number = null;
				}
			}

			replaceSystemVars(sb, number);

			if (mathType == MathType.constant){
				sb.append(mathTypeResult.getMatch());
				i += mathTypeResult.getMatch().length() - 1;
			} else if ( mathType == MathType.function) {
				sb.append(fromJsclFunction(mathTypeResult.getMatch()));
				i += mathTypeResult.getMatch().length() - 1;
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
		} else {
			number = null;
		}

		replaceSystemVars(sb, number);

		return removeMultiplicationSigns(sb.toString());
	}

	@NotNull
	private static String fromJsclFunction(@NotNull String function) {
		final String result;

		if (function.equals(Functions.LN_JSCL)) {
			result = Functions.LN;
		} else if (function.equals(Functions.SQRT_JSCL)) {
			result = Functions.SQRT;
		} else {
			result = function;
		}

		return result;
	}

	@NotNull
	private String removeMultiplicationSigns(String s) {
		final StringBuilder sb = new StringBuilder();

		MathType.Result mathTypeBefore;
		MathType.Result mathType = null;
		MathType.Result mathTypeAfter = null;

		for (int i = 0; i < s.length(); i++) {
			mathTypeBefore = mathType;
			if (mathTypeAfter == null) {
				mathType = MathType.getType(s, i);
			} else {
				mathType = mathTypeAfter;
			}

			char ch = s.charAt(i);
			if (ch == '*') {
				if (i + 1 < s.length()) {
					mathTypeAfter = MathType.getType(s, i + 1);
				} else {
					mathTypeAfter = null;
				}

				if (needMultiplicationSign(mathTypeBefore == null ? null : mathTypeBefore.getMathType(), mathTypeAfter == null ? null : mathTypeAfter.getMathType())) {
					sb.append("×");
				}

			} else {
				if (mathType.getMathType() == MathType.constant || mathType.getMathType() == MathType.function) {
					sb.append(mathType.getMatch());
					i += mathType.getMatch().length() - 1;
				} else {
					sb.append(ch);
				}
				mathTypeAfter = null;
			}

		}

		return sb.toString();
	}

	private final List<MathType> mathTypes = Arrays.asList(MathType.function, MathType.constant);

	private boolean needMultiplicationSign(@Nullable MathType mathTypeBefore, @Nullable MathType mathTypeAfter) {
		if (mathTypeBefore == null || mathTypeAfter == null) {
			return true;
		} else if (mathTypes.contains(mathTypeBefore) || mathTypes.contains(mathTypeAfter)) {
			return false;
		} else if ( mathTypeBefore == MathType.close_group_symbol ) {
			return false;
		} else if ( mathTypeAfter == MathType.open_group_symbol ) {
			return false;
		}

		return true;
	}

	@Nullable
	private MathType replaceSystemVars(StringBuilder sb, String number) {
		MathType result = null;

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
				result = MathType.constant;
			} else {
				sb.delete(sb.length() - number.length(), sb.length());
				sb.append(CalculatorEngine.instance.format(Double.valueOf(number)));
			}
		}

		return result;
	}

}
