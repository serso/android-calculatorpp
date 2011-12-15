/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import jscl.MathContext;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.function.IConstant;
import jscl.math.numeric.Real;
import jscl.text.*;
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

	@NotNull
	private final MathEngine engine;

	@Nullable
	private StringBuilder numberBuilder = null;

	private final boolean allowScientificFormat;

	@Nullable
	private NumeralBase nb;

	public NumberBuilder(boolean allowScientificFormat, @NotNull MathEngine engine) {
		this.allowScientificFormat = allowScientificFormat;
		this.nb = engine.getNumeralBase();
		this.engine = engine;
	}

	/**
	 * Method replaces number in text according to some rules (e.g. formatting)
	 *
	 * @param text text where number can be replaced
	 * @param mathTypeResult math type result of current token
	 * @param offset offset between new number length and old number length (newNumberLength - oldNumberLength)
	 *
	 *
	 * @return new math type result (as one can be changed due to substituting of number with constant)
	 */
	@NotNull
	public MathType.Result process(@NotNull StringBuilder text, @NotNull MathType.Result mathTypeResult, @Nullable MutableObject<Integer> offset) {
		final MathType.Result possibleResult;
		if (canContinue(mathTypeResult)) {
			// let's continue building number
			if (numberBuilder == null) {
				// if new number => create new builder
				numberBuilder = new StringBuilder();
			}

			if (mathTypeResult.getMathType() != MathType.numeral_base) {
				// just add matching string
				numberBuilder.append(mathTypeResult.getMatch());
			} else {
				// set explicitly numeral base (do not include it into number)
				nb = NumeralBase.getByPrefix(mathTypeResult.getMatch());
			}

			possibleResult = null;
		} else {
			// process current number (and go to the next one)
			possibleResult = processNumber(text, offset);
		}

		return possibleResult == null ? mathTypeResult : possibleResult;
	}

	/**
	 * Method determines if we can continue to process current number
	 * @param mathTypeResult current math type result
	 *
	 * @return true if we can continue of processing of current number, if false - new number should be constructed
	 */
	private boolean canContinue(@NotNull MathType.Result mathTypeResult) {
		return ((mathTypeResult.getMathType().getGroupType() == MathType.MathGroupType.number && numeralBaseCheck(mathTypeResult) && numeralBaseInTheStart(mathTypeResult.getMathType()) || isSignAfterE(mathTypeResult)));
	}

	private boolean numeralBaseInTheStart(@NotNull MathType mathType) {
		return mathType != MathType.numeral_base || numberBuilder == null;
	}

	private boolean numeralBaseCheck(@NotNull MathType.Result mathType) {
		if (mathType.getMathType() == MathType.digit) {
			final Character ch = mathType.getMatch().charAt(0);
			if (NumeralBase.hex.getAcceptableCharacters().contains(ch) && !NumeralBase.dec.getAcceptableCharacters().contains(ch)) {
				if (nb == NumeralBase.hex) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return true;
		}
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

	/**
	 * Method replaces number in text according to some rules (e.g. formatting)
	 *
	 * @param text text where number can be replaced
	 * @param offset offset between new number length and old number length (newNumberLength - oldNumberLength)
	 *
	 * @return new math type result (as one can be changed due to substituting of number with constant)
	 */
	@Nullable
	public MathType.Result processNumber(@NotNull StringBuilder text, @Nullable MutableObject<Integer> offset) {
		// total number of trimmed chars
		int trimmedChars = 0;

		String number = null;

		// save numeral base (as later it might be replaced)
		final NumeralBase localNb = getNumeralBase();

		if (numberBuilder != null) {
			try {
				number = numberBuilder.toString();

				// let's get rid of unnecessary characters (grouping separators, + after E)
				final List<String> tokens = new ArrayList<String>();
				tokens.addAll(MathType.grouping_separator.getTokens());
				// + after E can be omitted: 10+E = 10E (NOTE: - cannot be omitted )
				tokens.add("+");
				for (String groupingSeparator : tokens) {
					final String trimmedNumber = number.replace(groupingSeparator, "");
					trimmedChars += number.length() - trimmedNumber.length();
					number = trimmedNumber;
				}

				// check if number still valid
				toDouble(number, getNumeralBase(), engine);

			} catch (NumberFormatException e) {
				// number is not valid => stop
				number = null;
			}

			numberBuilder = null;

			// must set default numeral base (exit numeral base mode)
			nb = engine.getNumeralBase();
		}

		return replaceNumberInText(text, number, trimmedChars, offset, localNb, allowScientificFormat, engine);
	}

	@Nullable
	private static MathType.Result replaceNumberInText(@NotNull StringBuilder text,
													   @Nullable String number,
													   int trimmedChars,
													   @Nullable MutableObject<Integer> offset,
													   @NotNull NumeralBase nb,
													   boolean allowScientificFormat,
													   @NotNull final MathEngine engine) {
		MathType.Result result = null;

		if (number != null) {
			final String finalNumber = number;

			// detect if current number is precisely equals to constant in constants' registry  (NOTE: ONLY FOR SYSTEM CONSTANTS)
			final IConstant constant = CollectionsUtils.find(engine.getConstantsRegistry().getSystemEntities(), new Finder<IConstant>() {
				@Override
				public boolean isFound(@Nullable IConstant constant) {
					return constant != null && finalNumber.equals(constant.getValue());
				}
			});

			// in any case remove old number from text
			final int oldNumberLength = number.length() + trimmedChars;
			text.delete(text.length() - oldNumberLength, text.length());

			if (constant != null) {
				// let's change number with constant from registry
				text.append(constant.getName());
				result = new MathType.Result(MathType.constant, constant.getName());
			} else {
				final String newNumber = formatNumber(number, nb, allowScientificFormat, engine);
				if (offset != null) {
					// register offset between old number and new number
					offset.setObject(newNumber.length() - oldNumberLength);
				}
				text.append(newNumber);
			}
		}

		return result;
	}

	@NotNull
	private static String formatNumber(@NotNull String number, @NotNull NumeralBase nb, boolean allowScientificFormat, @NotNull MathEngine engine) {
		String result;

		if (allowScientificFormat) {
			int indexOfDot = number.indexOf('.');

			if (indexOfDot < 0) {
				int indexOfE;
				if (nb == NumeralBase.hex) {
					indexOfE = -1;
				} else {
					indexOfE = number.indexOf(MathType.POWER_10);
				}
				if (indexOfE < 0) {
					result = toString(number, nb, engine);
				} else {
					final String part;
					if (indexOfDot != 0) {
						part = toString(number.substring(0, indexOfE), nb, engine);
					} else {
						part = "";
					}
					result = part + number.substring(indexOfE);
				}
			} else {
				final String integerPart;
				if (indexOfDot != 0) {
					integerPart = toString(number.substring(0, indexOfDot), nb, engine);
				} else {
					integerPart = "";
				}
				result = integerPart + number.substring(indexOfDot);
			}
		} else {
			result = toString(number, nb, engine);
		}

		return result;
	}

	@NotNull
	private static String toString(@NotNull String value, @NotNull NumeralBase nb, @NotNull MathContext mathContext) {
		return mathContext.format(toDouble(value, nb, mathContext), nb);
	}

	public boolean isHexMode() {
		return nb == NumeralBase.hex || (nb == null && engine.getNumeralBase() == NumeralBase.hex);
	}

	@NotNull
	private NumeralBase getNumeralBase() {
		return nb == null ? engine.getNumeralBase() : nb;
	}

	@NotNull
	private static Double toDouble(@NotNull String s, @NotNull NumeralBase nb, @NotNull final MathContext mc) throws NumberFormatException {
		final NumeralBase defaultNb = mc.getNumeralBase();
		try {
			mc.setNumeralBase(nb);

			try {
				return JsclIntegerParser.parser.parse(Parser.Parameters.newInstance(s, new MutableInt(0), mc), null).content().doubleValue();
			} catch (ParseException e) {
				try {
					return ((Real) DoubleParser.parser.parse(Parser.Parameters.newInstance(s, new MutableInt(0), mc), null).content()).doubleValue();
				} catch (ParseException e1) {
					throw new NumberFormatException();
				}
			}

		} finally {
			mc.setNumeralBase(defaultNb);
		}
	}
}
