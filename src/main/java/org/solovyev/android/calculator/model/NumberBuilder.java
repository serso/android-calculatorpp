/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Finder;
import org.solovyev.common.utils.MutableObject;

/**
* User: serso
* Date: 10/23/11
* Time: 2:57 PM
*/
public class NumberBuilder {

	@Nullable
	private StringBuilder numberBuilder = null;
	@Nullable
	private String number = null;

	private final boolean simpleFormat;

	public NumberBuilder(boolean simpleFormat) {
		this.simpleFormat = simpleFormat;
	}

	@NotNull
	public MathType.Result process(@NotNull StringBuilder sb, @NotNull MathType.Result mathTypeResult, @Nullable MutableObject<Integer> numberOffset) {
		number = null;

		final MathType.Result possibleResult;
		if (CollectionsUtils.contains(mathTypeResult.getMathType(), MathType.digit, MathType.dot, MathType.grouping_separator, MathType.power_10)) {
			if (numberBuilder == null) {
				numberBuilder = new StringBuilder();
			}

			numberBuilder.append(mathTypeResult.getMatch());

			possibleResult = null;
		} else {
			possibleResult = process(sb, numberOffset);
		}

		return possibleResult == null ? mathTypeResult : possibleResult;
	}


	@Nullable
	public MathType.Result process(@NotNull StringBuilder sb, @Nullable MutableObject<Integer> numberOffset) {
		int numberOfGroupingSeparators = 0;

		if (numberBuilder != null) {
			try {
				number = numberBuilder.toString();
				for (String groupingSeparator : MathType.grouping_separator.getTokens()) {
					String newNumber = number.replace(groupingSeparator, "");
					numberOfGroupingSeparators += number.length() - newNumber.length();
					number = newNumber;
				}
				Double.valueOf(number);
			} catch (NumberFormatException e) {
				number = null;
			}

			numberBuilder = null;
		} else {
			number = null;
		}

		return replaceSystemVars(sb, number, numberOfGroupingSeparators, numberOffset);
	}

	@Nullable
	private MathType.Result replaceSystemVars(StringBuilder sb, String number, int numberOfGroupingSeparators, @Nullable MutableObject<Integer> numberOffset) {
		MathType.Result result = null;

		if (number != null) {
			final String finalNumber = number;
			final Var var = CollectionsUtils.get(CalculatorEngine.instance.getVarsRegister().getSystemVars(), new Finder<Var>() {
				@Override
				public boolean isFound(@Nullable Var var) {
					return var != null && finalNumber.equals(var.getValue());
				}
			});

			if (var != null) {
				sb.delete(sb.length() - number.length() - numberOfGroupingSeparators, sb.length());
				sb.append(var.getName());
				result = new MathType.Result(MathType.constant, var.getName());
			} else {
				sb.delete(sb.length() - number.length() - numberOfGroupingSeparators, sb.length());

				final String formattedNumber;

				if (!simpleFormat) {
					int indexOfDot = number.indexOf('.');

					if (indexOfDot < 0) {
						formattedNumber = CalculatorEngine.instance.format(Double.valueOf(number), false);
					} else {
						String integerPart = null;
						if (indexOfDot != 0) {
							integerPart = CalculatorEngine.instance.format(Double.valueOf(number.substring(0, indexOfDot)), false);
						} else {
							integerPart = "";
						}
						formattedNumber = integerPart + number.substring(indexOfDot);
					}
				} else {
					formattedNumber = CalculatorEngine.instance.format(Double.valueOf(number), true);
				}

				if (numberOffset != null) {
					numberOffset.setObject(formattedNumber.length() - number.length() - numberOfGroupingSeparators);
				}
				sb.append(formattedNumber);
			}
		}

		return result;
	}
}
