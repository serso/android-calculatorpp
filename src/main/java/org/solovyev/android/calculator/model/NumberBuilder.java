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

import java.util.ArrayList;
import java.util.List;

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
		if (CollectionsUtils.contains(mathTypeResult.getMathType(), MathType.digit, MathType.dot, MathType.grouping_separator, MathType.power_10) ||
				isSignAfterE(mathTypeResult)) {
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

	private boolean isSignAfterE(@NotNull MathType.Result mathTypeResult) {
		if ("-".equals(mathTypeResult.getMatch()) || "+".equals(mathTypeResult.getMatch())) {
			if (numberBuilder != null && numberBuilder.length() > 0) {
				if (numberBuilder.charAt(numberBuilder.length() - 1) == MathType.POWER_10) {
					return true;
				}
			}
		}
		return false;
	}


	@Nullable
	public MathType.Result process(@NotNull StringBuilder sb, @Nullable MutableObject<Integer> numberOffset) {
		int numberOfTokens = 0;

		if (numberBuilder != null) {
			try {
				number = numberBuilder.toString();
				List<String> tokens = new ArrayList<String>();
				tokens.addAll(MathType.grouping_separator.getTokens());
				tokens.add("+");
				for (String groupingSeparator : tokens) {
					String newNumber = number.replace(groupingSeparator, "");
					numberOfTokens += number.length() - newNumber.length();
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

		return replaceSystemVars(sb, number, numberOfTokens, numberOffset);
	}

	@Nullable
	private MathType.Result replaceSystemVars(StringBuilder sb, String number, int numberOfTokens, @Nullable MutableObject<Integer> numberOffset) {
		MathType.Result result = null;

		if (number != null) {
			final String finalNumber = number;
			final Var var = CollectionsUtils.find(CalculatorEngine.instance.getVarsRegister().getSystemEntities(), new Finder<Var>() {
				@Override
				public boolean isFound(@Nullable Var var) {
					return var != null && finalNumber.equals(var.getValue());
				}
			});

			if (var != null) {
				sb.delete(sb.length() - number.length() - numberOfTokens, sb.length());
				sb.append(var.getName());
				result = new MathType.Result(MathType.constant, var.getName());
			} else {
				sb.delete(sb.length() - number.length() - numberOfTokens, sb.length());

				final String formattedNumber;

				if (!simpleFormat) {
					int indexOfDot = number.indexOf('.');

					if (indexOfDot < 0) {
						int indexOfE = number.indexOf('E');
						if (indexOfE < 0) {
							formattedNumber = CalculatorEngine.instance.format(Double.valueOf(number), false);
						} else {
							final String part;
							if (indexOfDot != 0) {
								part = CalculatorEngine.instance.format(Double.valueOf(number.substring(0, indexOfE)), false);
							} else {
								part = "";
							}
							formattedNumber = part + number.substring(indexOfE);
						}
					} else {
						final String integerPart;
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
					numberOffset.setObject(formattedNumber.length() - number.length() - numberOfTokens);
				}
				sb.append(formattedNumber);
			}
		}

		return result;
	}
}
